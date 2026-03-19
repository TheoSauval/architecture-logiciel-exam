package com.coworking.reservationservice.pattern;

import com.coworking.reservationservice.model.Reservation;

public interface ReservationState {
    Reservation cancel(Reservation reservation);
    Reservation complete(Reservation reservation);
}
