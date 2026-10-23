package com.petgrooming.appointments.groomer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GroomerTest {

    @Test
    void createsGroomerAndBuildsFullName() {
        Groomer groomer = new Groomer(1L, " Alex ", " Rivera ", "alex@example.test", " ", true);

        assertEquals("Alex", groomer.firstName());
        assertEquals("Rivera", groomer.lastName());
        assertEquals("Alex Rivera", groomer.fullName());
        assertNull(groomer.phone());
    }

    @Test
    void rejectsBlankEmail() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Groomer(null, "Alex", "Rivera", "", null, true)
        );
    }
}
