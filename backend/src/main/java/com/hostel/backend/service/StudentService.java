package com.hostel.backend.service;

import com.hostel.backend.dto.StudentRequest;
import com.hostel.backend.entity.Student;
import com.hostel.backend.repository.StudentRepository;
import com.hostel.backend.repository.AllocationRepository;
import com.hostel.backend.repository.FeeRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository repository;
    private final AllocationRepository allocations;
    private final FeeRecordRepository fees;
    public StudentService(StudentRepository repository, AllocationRepository allocations, FeeRecordRepository fees) {
        this.repository = repository;
        this.allocations = allocations;
        this.fees = fees;
    }
    public List<Student> all() { return repository.findAll(); }
    public Student get(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found")); }
    @Transactional
    public Student create(StudentRequest request) {
        if (repository.existsByRegistrationNumber(request.registrationNumber())) throw new ConflictException("Registration number already exists");
        if (repository.existsByEmail(request.email())) throw new ConflictException("Email already exists");
        return save(new Student(request.registrationNumber(), request.name(), request.email(), request.phone(), request.course(), request.address()));
    }
    @Transactional
    public Student update(Long id, StudentRequest request) {
        Student student = get(id);
        if (!student.getRegistrationNumber().equals(request.registrationNumber()) && repository.existsByRegistrationNumber(request.registrationNumber())) throw new ConflictException("Registration number already exists");
        if (!student.getEmail().equals(request.email()) && repository.existsByEmail(request.email())) throw new ConflictException("Email already exists");
        student.setRegistrationNumber(request.registrationNumber()); student.setName(request.name()); student.setEmail(request.email());
        student.setPhone(request.phone()); student.setCourse(request.course()); student.setAddress(request.address());
        return save(student);
    }
    public void delete(Long id) {
        Student student = get(id);
        if (allocations.existsByStudentId(id)) throw new ConflictException("Cannot delete a student with an active allocation");
        if (fees.existsByStudentId(id)) throw new ConflictException("Cannot delete a student with fee records");
        repository.delete(student);
    }
    private Student save(Student value) { return repository.save(value); }
}
