package com.coworking.reservationservice.pattern;

import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.model.ReservationStatus;

public class ConfirmedState implements ReservationState {

    @Override
    public Reservation cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservation;
    }

    @Override
    public Reservation complete(Reservation reservation) {
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservation;
    }
}
