package com.coworking.reservationservice.repository;

import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomIdAndStatus(Long roomId, ReservationStatus status);

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByMemberIdAndStatus(Long memberId, ReservationStatus status);

    boolean existsByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long roomId, ReservationStatus status, LocalDateTime end, LocalDateTime start);
}
