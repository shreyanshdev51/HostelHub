package com.hostel.backend.service;

import com.hostel.backend.dto.RoomRequest;
import com.hostel.backend.entity.Room;
import com.hostel.backend.repository.AllocationRepository;
import com.hostel.backend.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock RoomRepository rooms;
    @Mock AllocationRepository allocations;
    @InjectMocks RoomService service;

    @Test
    void rejectsDuplicateRoomNumber() {
        when(rooms.existsByRoomNumber("101")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(new RoomRequest("101", 2, 1, "AVAILABLE")));
        verify(rooms, never()).save(any());
    }

    @Test
    void createsRoom() {
        when(rooms.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RoomRequest request = new RoomRequest("102", 2, 1, "AVAILABLE");
        assertEquals("102", service.create(request).getRoomNumber());
        verify(rooms).save(any());
    }

    @Test
    void updatesRoom() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setCapacity(2);
        when(rooms.findById(1L)).thenReturn(java.util.Optional.of(room));
        when(allocations.countByRoomId(1L)).thenReturn(1L);
        when(rooms.save(room)).thenReturn(room);
        Room result = service.update(1L, new RoomRequest("103", 2, 2, "MAINTENANCE"));
        assertEquals("103", result.getRoomNumber());
        assertEquals("MAINTENANCE", result.getStatus());
        verify(rooms).save(room);
    }

    @Test
    void deletesEmptyRoom() {
        Room room = new Room();
        when(rooms.findById(1L)).thenReturn(java.util.Optional.of(room));
        when(allocations.countByRoomId(1L)).thenReturn(0L);
        service.delete(1L);
        verify(rooms).delete(room);
    }
}
