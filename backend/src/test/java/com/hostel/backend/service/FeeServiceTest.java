package com.hostel.backend.service;

import com.hostel.backend.repository.FeeRecordRepository;
import com.hostel.backend.repository.StudentRepository;
import com.hostel.backend.dto.FeeRequest;
import com.hostel.backend.entity.FeeRecord;
import com.hostel.backend.entity.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {
    @Mock FeeRecordRepository fees;
    @Mock StudentRepository students;
    @InjectMocks FeeService service;

    @Test
    void missingFeeDeleteReturnsNotFound() {
        when(fees.findById(99L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(fees, never()).delete(any());
    }

    @Test
    void createsFeeForExistingStudent() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(fees.save(any(FeeRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        FeeRecord result = service.create(new FeeRequest(1L, new BigDecimal("100.00"), LocalDate.now(), null, "PENDING"));
        assertSame(student, result.getStudent());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(fees).save(any(FeeRecord.class));
    }

    @Test
    void updatesFee() {
        Student student = new Student("REG-1", "Student", "student@example.com", "123", "CS", null);
        FeeRecord fee = new FeeRecord();
        when(fees.findById(1L)).thenReturn(java.util.Optional.of(fee));
        when(students.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(fees.save(fee)).thenReturn(fee);
        FeeRecord result = service.update(1L, new FeeRequest(1L, new BigDecimal("250.00"), LocalDate.now(), LocalDate.now(), "PAID"));
        assertSame(student, result.getStudent());
        assertEquals("PAID", result.getStatus());
        verify(fees).save(fee);
    }

    @Test
    void deletesExistingFee() {
        FeeRecord fee = new FeeRecord();
        when(fees.findById(1L)).thenReturn(java.util.Optional.of(fee));
        service.delete(1L);
        verify(fees).delete(fee);
    }
}
