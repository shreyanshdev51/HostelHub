package com.hostel.backend.controller;
import com.hostel.backend.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final StudentRepository students; private final RoomRepository rooms; private final AllocationRepository allocations; private final FeeRecordRepository fees;
    public DashboardController(StudentRepository s, RoomRepository r, AllocationRepository a, FeeRecordRepository f) { students=s; rooms=r; allocations=a; fees=f; }
    @GetMapping
    public Map<String, Long> summary() {
        return Map.of("students", students.count(), "rooms", rooms.count(), "allocatedBeds", allocations.count(), "pendingFees", fees.countByStatusIgnoreCase("PENDING"));
    }
}
