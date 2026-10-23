package com.hostel.backend.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record FeeRequest(@NotNull Long studentId, @NotNull @DecimalMin("0.01") BigDecimal amount,
                         @NotNull LocalDate dueDate, LocalDate paidDate, @NotBlank String status) {}
