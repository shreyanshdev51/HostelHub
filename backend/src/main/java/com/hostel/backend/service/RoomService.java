package com.hostel.backend.service;

import com.hostel.backend.dto.RoomRequest;
import com.hostel.backend.entity.Room;
import com.hostel.backend.repository.AllocationRepository;
import com.hostel.backend.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository rooms;
    private final AllocationRepository allocations;
    public RoomService(RoomRepository rooms, AllocationRepository allocations) { this.rooms = rooms; this.allocations = allocations; }
    public List<Room> all() { return rooms.findAll(); }
    public Room get(Long id) { return rooms.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found")); }
    @Transactional
    public Room create(RoomRequest request) {
        if (rooms.existsByRoomNumber(request.roomNumber())) throw new ConflictException("Room number already exists");
        Room room = new Room(); room.setRoomNumber(request.roomNumber()); room.setCapacity(request.capacity());
        room.setFloor(request.floor()); room.setStatus(request.status()); return rooms.save(room);
    }
    @Transactional
    public Room update(Long id, RoomRequest request) {
        Room room = get(id); long occupied = allocations.countByRoomId(id);
        if (request.capacity() < occupied) throw new ConflictException("Capacity cannot be lower than current occupancy");
        if (!room.getRoomNumber().equals(request.roomNumber()) && rooms.existsByRoomNumber(request.roomNumber())) throw new ConflictException("Room number already exists");
        room.setRoomNumber(request.roomNumber()); room.setCapacity(request.capacity()); room.setFloor(request.floor()); room.setStatus(request.status());
        return rooms.save(room);
    }
    public void delete(Long id) {
        if (allocations.countByRoomId(id) > 0) throw new ConflictException("Cannot delete a room with allocated students");
        rooms.delete(get(id));
    }
}
