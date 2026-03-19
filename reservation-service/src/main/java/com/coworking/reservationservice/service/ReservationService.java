package com.coworking.reservationservice.service;

import com.coworking.reservationservice.dto.MemberDto;
import com.coworking.reservationservice.dto.RoomDto;
import com.coworking.reservationservice.kafka.ReservationEventProducer;
import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.model.ReservationStatus;
import com.coworking.reservationservice.pattern.ReservationStateFactory;
import com.coworking.reservationservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate;
    private final ReservationEventProducer eventProducer;

    @Value("${room-service.url:http://room-service}")
    private String roomServiceUrl;

    @Value("${member-service.url:http://member-service}")
    private String memberServiceUrl;

    public ReservationService(ReservationRepository reservationRepository,
                              RestTemplate restTemplate,
                              ReservationEventProducer eventProducer) {
        this.reservationRepository = reservationRepository;
        this.restTemplate = restTemplate;
        this.eventProducer = eventProducer;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable : " + id));
    }

    public Reservation createReservation(Reservation reservation) {
        // Vérifier que la salle existe et est disponible
        RoomDto room = restTemplate.getForObject(
                roomServiceUrl + "/rooms/" + reservation.getRoomId(), RoomDto.class);
        if (room == null || !room.isAvailable()) {
            throw new RuntimeException("Salle non disponible ou introuvable");
        }

        // Vérifier que le membre existe et n'est pas suspendu
        MemberDto member = restTemplate.getForObject(
                memberServiceUrl + "/members/" + reservation.getMemberId(), MemberDto.class);
        if (member == null || member.isSuspended()) {
            throw new RuntimeException("Membre suspendu ou introuvable");
        }

        // Vérifier qu'il n'y a pas de chevauchement sur ce créneau
        boolean overlap = reservationRepository
                .existsByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        reservation.getRoomId(),
                        ReservationStatus.CONFIRMED,
                        reservation.getEndDateTime(),
                        reservation.getStartDateTime());
        if (overlap) {
            throw new RuntimeException("La salle est déjà réservée sur ce créneau");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation saved = reservationRepository.save(reservation);

        // Marquer la salle comme indisponible
        restTemplate.put(roomServiceUrl + "/rooms/" + reservation.getRoomId()
                + "/availability?available=false", null);

        // Vérifier le quota du membre
        long activeCount = reservationRepository
                .findByMemberIdAndStatus(reservation.getMemberId(), ReservationStatus.CONFIRMED).size();
        if (activeCount >= member.getMaxConcurrentBookings()) {
            eventProducer.sendMemberSuspended(reservation.getMemberId());
        }

        return saved;
    }

    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        // Applique la transition via le State Pattern
        reservation = ReservationStateFactory.getState(reservation.getStatus()).cancel(reservation);
        Reservation saved = reservationRepository.save(reservation);

        // Rendre la salle disponible
        restTemplate.put(roomServiceUrl + "/rooms/" + reservation.getRoomId()
                + "/availability?available=true", null);

        // Vérifier si le membre doit être désuspendu
        checkAndUnsuspendMember(reservation.getMemberId());

        return saved;
    }

    public Reservation completeReservation(Long id) {
        Reservation reservation = getReservationById(id);
        reservation = ReservationStateFactory.getState(reservation.getStatus()).complete(reservation);
        Reservation saved = reservationRepository.save(reservation);

        // Rendre la salle disponible
        restTemplate.put(roomServiceUrl + "/rooms/" + reservation.getRoomId()
                + "/availability?available=true", null);

        checkAndUnsuspendMember(reservation.getMemberId());

        return saved;
    }

    public void deleteReservation(Long id) {
        getReservationById(id);
        reservationRepository.deleteById(id);
    }

    private void checkAndUnsuspendMember(Long memberId) {
        MemberDto member = restTemplate.getForObject(
                memberServiceUrl + "/members/" + memberId, MemberDto.class);
        if (member != null && member.isSuspended()) {
            long activeCount = reservationRepository
                    .findByMemberIdAndStatus(memberId, ReservationStatus.CONFIRMED).size();
            if (activeCount < member.getMaxConcurrentBookings()) {
                eventProducer.sendMemberUnsuspended(memberId);
            }
        }
    }
}
