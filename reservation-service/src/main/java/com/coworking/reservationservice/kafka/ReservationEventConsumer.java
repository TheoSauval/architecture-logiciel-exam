package com.coworking.reservationservice.kafka;

import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.model.ReservationStatus;
import com.coworking.reservationservice.repository.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationEventConsumer {

    private final ReservationRepository reservationRepository;

    public ReservationEventConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @KafkaListener(topics = "room-deleted", groupId = "reservation-service-group")
    public void onRoomDeleted(String roomId) {
        List<Reservation> confirmed = reservationRepository
                .findByRoomIdAndStatus(Long.parseLong(roomId), ReservationStatus.CONFIRMED);
        confirmed.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        reservationRepository.saveAll(confirmed);
    }

    @KafkaListener(topics = "member-deleted", groupId = "reservation-service-group")
    public void onMemberDeleted(String memberId) {
        List<Reservation> reservations = reservationRepository
                .findByMemberId(Long.parseLong(memberId));
        reservationRepository.deleteAll(reservations);
    }
}
