package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AppointmentStatusTest {

    @Test
    void exposesExpectedStatuses() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.valueOf("PENDING"));
        assertEquals(AppointmentStatus.CONFIRMED, AppointmentStatus.valueOf("CONFIRMED"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.valueOf("COMPLETED"));
        assertEquals(AppointmentStatus.CANCELLED, AppointmentStatus.valueOf("CANCELLED"));
    }

    @Test
    void rejectsUnknownStatus() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentStatus.valueOf("UNKNOWN"));
    }
}
