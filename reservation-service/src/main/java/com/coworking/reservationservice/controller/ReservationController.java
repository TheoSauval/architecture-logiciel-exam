package com.coworking.reservationservice.controller;

import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réservations")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    @Operation(summary = "Créer une réservation (avec validations cross-service)")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservation));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une réservation par ID")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une réservation (State Pattern)")
    public Reservation cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Marquer une réservation comme terminée (State Pattern)")
    public Reservation completeReservation(@PathVariable Long id) {
        return reservationService.completeReservation(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
