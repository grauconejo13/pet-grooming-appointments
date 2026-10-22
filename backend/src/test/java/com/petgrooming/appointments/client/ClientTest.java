package com.petgrooming.appointments.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void createsClientAndBuildsFullName() {
        Client client = new Client(1L, " Jordan ", " Lee ", "jordan@example.test", "5550100");

        assertEquals("Jordan", client.firstName());
        assertEquals("Lee", client.lastName());
        assertEquals("Jordan Lee", client.fullName());
    }

    @Test
    void rejectsBlankRequiredFields() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Client(null, "", "Lee", "jordan@example.test", "5550100")
        );
    }
}
