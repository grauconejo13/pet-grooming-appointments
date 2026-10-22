package com.petgrooming.appointments.pet;

import java.time.LocalDate;

public record Pet(
    Long id,
    Long clientId,
    String name,
    String species,
    String breed,
    LocalDate birthDate,
    String size,
    String careNotes
) {
    public Pet {
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("clientId must be positive");
        }
        name = requireText(name, "name");
        species = requireText(species, "species");
        size = requireText(size, "size");
        breed = normalizeOptional(breed);
        careNotes = normalizeOptional(careNotes);
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
