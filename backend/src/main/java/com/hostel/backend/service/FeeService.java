package com.hostel.backend.service;

import com.hostel.backend.dto.FeeRequest;
import com.hostel.backend.entity.FeeRecord;
import com.hostel.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FeeService {
    private final FeeRecordRepository fees; private final StudentRepository students;
    public FeeService(FeeRecordRepository f, StudentRepository s) { fees = f; students = s; }
    public List<FeeRecord> all() { return fees.findAll(); }
    @Transactional
    public FeeRecord create(FeeRequest request) {
        FeeRecord fee = new FeeRecord();
        fee.setStudent(students.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        fee.setAmount(request.amount()); fee.setDueDate(request.dueDate()); fee.setPaidDate(request.paidDate()); fee.setStatus(request.status());
        return fees.save(fee);
    }
    @Transactional
    public FeeRecord update(Long id, FeeRequest request) {
        FeeRecord fee = fees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fee record not found"));
        fee.setStudent(students.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        fee.setAmount(request.amount()); fee.setDueDate(request.dueDate()); fee.setPaidDate(request.paidDate()); fee.setStatus(request.status());
        return fees.save(fee);
    }
    public void delete(Long id) { fees.delete(fees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fee record not found"))); }
}
