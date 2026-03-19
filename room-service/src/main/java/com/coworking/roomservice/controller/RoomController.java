package com.coworking.roomservice.controller;

import com.coworking.roomservice.model.Room;
import com.coworking.roomservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Gestion des salles de coworking")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les salles")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    @Operation(summary = "Créer une salle")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(room));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une salle par ID")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une salle")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une salle (publie un événement Kafka)")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/availability")
    @Operation(summary = "Mettre à jour la disponibilité d'une salle")
    public Room updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return roomService.updateAvailability(id, available);
    }
}
