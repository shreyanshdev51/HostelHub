package com.hostel.backend.repository;

import com.hostel.backend.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    boolean existsByStudentId(Long studentId);
    long countByRoomId(Long roomId);
}
