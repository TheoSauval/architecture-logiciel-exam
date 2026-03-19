package com.coworking.reservationservice.pattern;

import com.coworking.reservationservice.model.Reservation;

public class CompletedState implements ReservationState {

    @Override
    public Reservation cancel(Reservation reservation) {
        throw new IllegalStateException("Impossible d'annuler une réservation terminée");
    }

    @Override
    public Reservation complete(Reservation reservation) {
        throw new IllegalStateException("Réservation déjà terminée");
    }
}
