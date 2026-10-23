package com.hostel.backend.service;

import com.hostel.backend.dto.StudentRequest;
import com.hostel.backend.entity.Student;
import com.hostel.backend.repository.AllocationRepository;
import com.hostel.backend.repository.FeeRecordRepository;
import com.hostel.backend.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock StudentRepository students;
    @Mock AllocationRepository allocations;
    @Mock FeeRecordRepository fees;
    @InjectMocks StudentService service;

    @Test
    void rejectsDuplicateRegistrationNumber() {
        when(students.existsByRegistrationNumber("REG-1")).thenReturn(true);
        StudentRequest request = new StudentRequest("REG-1", "Student", "student@example.com", "123", "CS", null);
        assertThrows(ConflictException.class, () -> service.create(request));
        verify(students, never()).save(any());
    }

    @Test
    void preventsDeletingAllocatedStudent() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(allocations.existsByStudentId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.delete(1L));
        verify(students, never()).delete(any());
    }

    @Test
    void preventsDeletingStudentWithFeeRecords() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(allocations.existsByStudentId(1L)).thenReturn(false);
        when(fees.existsByStudentId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.delete(1L));
        verify(students, never()).delete(any());
    }

    @Test
    void createsStudent() {
        StudentRequest request = new StudentRequest("REG-2", "Student", "student2@example.com", "456", "CS", "Address");
        when(students.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Student result = service.create(request);
        assertEquals("REG-2", result.getRegistrationNumber());
        verify(students).save(any(Student.class));
    }

    @Test
    void updatesStudent() {
        Student student = new Student("REG-1", "Old", "old@example.com", "123", "CS", null);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(students.save(student)).thenReturn(student);
        Student result = service.update(1L, new StudentRequest("REG-1", "New", "new@example.com", "456", "IT", "Address"));
        assertEquals("New", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(students).save(student);
    }

    @Test
    void deletesUnallocatedStudent() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(allocations.existsByStudentId(1L)).thenReturn(false);
        service.delete(1L);
        verify(students).delete(student);
    }
}
