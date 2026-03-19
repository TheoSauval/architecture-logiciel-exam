package com.coworking.reservationservice.pattern;

import com.coworking.reservationservice.model.ReservationStatus;

public class ReservationStateFactory {

    public static ReservationState getState(ReservationStatus status) {
        return switch (status) {
            case CONFIRMED -> new ConfirmedState();
            case CANCELLED -> new CancelledState();
            case COMPLETED -> new CompletedState();
        };
    }
}
