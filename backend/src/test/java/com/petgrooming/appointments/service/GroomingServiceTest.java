package com.petgrooming.appointments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GroomingServiceTest {

    @Test
    void createsServiceAndNormalizesDescription() {
        GroomingService service = new GroomingService(
            1L,
            " Full Groom ",
            " ",
            90,
            new BigDecimal("85.00"),
            true
        );

        assertEquals("Full Groom", service.name());
        assertNull(service.description());
        assertEquals(90, service.durationMinutes());
    }

    @Test
    void rejectsNonPositiveDuration() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new GroomingService(null, "Bath", null, 0, new BigDecimal("35.00"), true)
        );
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new GroomingService(null, "Bath", null, 30, new BigDecimal("-1.00"), true)
        );
    }
}
