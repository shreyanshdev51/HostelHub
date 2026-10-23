package com.hostel.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "room_allocations", uniqueConstraints = @UniqueConstraint(name = "uk_allocation_student", columnNames = "student_id"))
public class Allocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate allocatedAt;

    public Allocation() {}
    public Allocation(Student student, Room room, LocalDate allocatedAt) {
        this.student = student;
        this.room = room;
        this.allocatedAt = allocatedAt;
    }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Room getRoom() { return room; }
    public LocalDate getAllocatedAt() { return allocatedAt; }
}
