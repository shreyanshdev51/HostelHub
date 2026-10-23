package com.hostel.backend.dto;
import jakarta.validation.constraints.*;
public record StudentRequest(@NotBlank String registrationNumber, @NotBlank @Size(max = 100) String name,
                              @NotBlank @Email String email, @NotBlank String phone,
                              @NotBlank String course, String address) {}
