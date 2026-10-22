package com.petgrooming.appointments.client;

public record Client(Long id, String firstName, String lastName, String email, String phone) {
    public Client {
        firstName = requireText(firstName, "firstName");
        lastName = requireText(lastName, "lastName");
        email = requireText(email, "email");
        phone = requireText(phone, "phone");
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
}
