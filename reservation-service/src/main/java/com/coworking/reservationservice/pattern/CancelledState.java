package com.coworking.reservationservice.pattern;

import com.coworking.reservationservice.model.Reservation;

public class CancelledState implements ReservationState {

    @Override
    public Reservation cancel(Reservation reservation) {
        throw new IllegalStateException("Réservation déjà annulée");
    }

    @Override
    public Reservation complete(Reservation reservation) {
        throw new IllegalStateException("Impossible de terminer une réservation annulée");
    }
}
