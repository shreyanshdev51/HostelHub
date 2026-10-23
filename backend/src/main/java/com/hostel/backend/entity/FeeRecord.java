package com.hostel.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_records")
public class FeeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate paidDate;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    public FeeRecord() {}
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public void setStudent(Student value) { this.student = value; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal value) { this.amount = value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate = value; }
    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate value) { this.paidDate = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
