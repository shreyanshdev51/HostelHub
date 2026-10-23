package com.hostel.backend.service;

import com.hostel.backend.dto.AllocationRequest;
import com.hostel.backend.entity.Room;
import com.hostel.backend.entity.Student;
import com.hostel.backend.entity.Allocation;
import com.hostel.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {
    @Mock AllocationRepository allocations;
    @Mock StudentRepository students;
    @Mock RoomRepository rooms;
    @InjectMocks AllocationService service;

    @Test
    void rejectsDuplicateStudentAllocation() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        Room room = new Room();
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(rooms.findById(2L)).thenReturn(java.util.Optional.of(room));
        when(allocations.existsByStudentId(any())).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(new AllocationRequest(1L, 2L)));
        verify(allocations, never()).save(any());
    }

    @Test
    void rejectsFullRoom() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        Room room = new Room();
        room.setCapacity(1);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(rooms.findById(2L)).thenReturn(java.util.Optional.of(room));
        when(allocations.existsByStudentId(isNull())).thenReturn(false);
        when(allocations.countByRoomId(isNull())).thenReturn(1L);
        assertThrows(ConflictException.class, () -> service.create(new AllocationRequest(1L, 2L)));
        verify(allocations, never()).save(any());
    }

    @Test
    void createsAllocationForAvailableRoom() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        Room room = new Room();
        room.setCapacity(2);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(rooms.findById(2L)).thenReturn(java.util.Optional.of(room));
        when(allocations.existsByStudentId(any())).thenReturn(false);
        when(allocations.countByRoomId(any())).thenReturn(0L);
        when(allocations.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Allocation result = service.create(new AllocationRequest(1L, 2L));
        assertSame(student, result.getStudent());
        assertSame(room, result.getRoom());
        verify(allocations).save(any());
    }

    @Test
    void deletesExistingAllocation() {
        Allocation allocation = new Allocation();
        when(allocations.findById(1L)).thenReturn(java.util.Optional.of(allocation));
        service.delete(1L);
        verify(allocations).delete(allocation);
    }
}
