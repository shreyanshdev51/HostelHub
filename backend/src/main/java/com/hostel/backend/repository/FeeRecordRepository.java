package com.hostel.backend.repository;

import com.hostel.backend.entity.FeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FeeRecordRepository extends JpaRepository<FeeRecord, Long> {
    long countByStatusIgnoreCase(String status);
    boolean existsByStudentId(Long studentId);
}
