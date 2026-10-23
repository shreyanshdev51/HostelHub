package com.hostel.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Min(1)
    @Column(nullable = false)
    private int capacity;

    @Min(0)
    @Column(nullable = false)
    private int floor;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String status = "AVAILABLE";

    public Room() {}
    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String value) { this.roomNumber = value; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int value) { this.capacity = value; }
    public int getFloor() { return floor; }
    public void setFloor(int value) { this.floor = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
