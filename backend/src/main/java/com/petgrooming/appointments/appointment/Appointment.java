package com.petgrooming.appointments.appointment;

import java.time.LocalDateTime;

public record Appointment(
    Long id,
    Long petId,
    Long groomerId,
    Long serviceId,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    AppointmentStatus status,
    String clientNote,
    String groomerNote
) {
    public Appointment {
        requirePositive(petId, "petId");
        requirePositive(groomerId, "groomerId");
        requirePositive(serviceId, "serviceId");
        if (startsAt == null || endsAt == null) {
            throw new IllegalArgumentException("startsAt and endsAt are required");
        }
        if (!endsAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
        clientNote = normalizeOptional(clientNote);
        groomerNote = normalizeOptional(groomerNote);
    }

    private static void requirePositive(Long value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
