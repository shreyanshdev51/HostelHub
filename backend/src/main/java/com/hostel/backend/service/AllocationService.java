package com.hostel.backend.service;

import com.hostel.backend.dto.AllocationRequest;
import com.hostel.backend.entity.*;
import com.hostel.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class AllocationService {
    private final AllocationRepository allocations; private final StudentRepository students; private final RoomRepository rooms;
    public AllocationService(AllocationRepository a, StudentRepository s, RoomRepository r) { allocations = a; students = s; rooms = r; }
    public List<Allocation> all() { return allocations.findAll(); }
    @Transactional
    public Allocation create(AllocationRequest request) {
        Student student = students.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Room room = rooms.findById(request.roomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (allocations.existsByStudentId(student.getId())) throw new ConflictException("Student already has a room");
        if (allocations.countByRoomId(room.getId()) >= room.getCapacity()) throw new ConflictException("Room is at full capacity");
        return allocations.save(new Allocation(student, room, LocalDate.now()));
    }
    public void delete(Long id) { allocations.delete(allocations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Allocation not found"))); }
}
