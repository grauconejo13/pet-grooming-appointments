package com.petgrooming.appointments.service;

import java.math.BigDecimal;

public record GroomingService(
    Long id,
    String name,
    String description,
    int durationMinutes,
    BigDecimal price,
    boolean active
) {
    public GroomingService {
        name = requireText(name, "name");
        description = normalizeOptional(description);
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes must be positive");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("price must be zero or greater");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
