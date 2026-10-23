package com.hostel.backend.dto;
import jakarta.validation.constraints.*;
public record RoomRequest(@NotBlank String roomNumber, @Min(1) int capacity, @Min(0) int floor,
                          @NotBlank String status) {}
