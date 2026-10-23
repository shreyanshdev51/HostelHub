package com.hostel.backend.repository;

import com.hostel.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByEmail(String email);
}
