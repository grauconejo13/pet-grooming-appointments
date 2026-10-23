package com.petgrooming.appointments.groomer;

public record Groomer(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    boolean active
) {
    public Groomer {
        firstName = requireText(firstName, "firstName");
        lastName = requireText(lastName, "lastName");
        email = requireText(email, "email");
        phone = normalizeOptional(phone);
    }

    public String fullName() {
        return firstName + " " + lastName;
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
