package com.petgrooming.appointments.groomingservice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GroomingServiceResponse(
        Long id,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        boolean active,
        LocalDateTime createdAt
) {
    static GroomingServiceResponse from(GroomingService groomingService) {
        return new GroomingServiceResponse(
                groomingService.getId(),
                groomingService.getName(),
                groomingService.getDescription(),
                groomingService.getDurationMinutes(),
                groomingService.getPrice(),
                groomingService.isActive(),
                groomingService.getCreatedAt()
        );
    }
}
