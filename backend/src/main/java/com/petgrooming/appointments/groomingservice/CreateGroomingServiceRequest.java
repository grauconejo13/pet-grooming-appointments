package com.petgrooming.appointments.groomingservice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateGroomingServiceRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 2_000) String description,
        @NotNull @Positive Integer durationMinutes,
        @NotNull @DecimalMin(value = "0.00") BigDecimal price
) {
}
