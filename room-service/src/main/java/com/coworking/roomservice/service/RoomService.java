package com.coworking.roomservice.service;

import com.coworking.roomservice.kafka.RoomEventProducer;
import com.coworking.roomservice.model.Room;
import com.coworking.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomEventProducer roomEventProducer;

    public RoomService(RoomRepository roomRepository, RoomEventProducer roomEventProducer) {
        this.roomRepository = roomRepository;
        this.roomEventProducer = roomEventProducer;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable : " + id));
    }

    public Room createRoom(Room room) {
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updated) {
        Room room = getRoomById(id);
        room.setName(updated.getName());
        room.setCity(updated.getCity());
        room.setCapacity(updated.getCapacity());
        room.setType(updated.getType());
        room.setHourlyRate(updated.getHourlyRate());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        getRoomById(id);
        roomEventProducer.sendRoomDeleted(id);
        roomRepository.deleteById(id);
    }

    public Room updateAvailability(Long id, boolean available) {
        Room room = getRoomById(id);
        room.setAvailable(available);
        return roomRepository.save(room);
    }
}
