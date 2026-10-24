package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppointmentStatusTest {

    @Test
    void allowsExpectedForwardTransitions() {
        assertTrue(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.CONFIRMED));
        assertTrue(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.CANCELLED));
        assertTrue(AppointmentStatus.CONFIRMED.canTransitionTo(AppointmentStatus.COMPLETED));
        assertTrue(AppointmentStatus.CONFIRMED.canTransitionTo(AppointmentStatus.CANCELLED));
    }

    @Test
    void rejectsBackwardSameAndTerminalTransitions() {
        assertFalse(AppointmentStatus.CONFIRMED.canTransitionTo(AppointmentStatus.PENDING));
        assertFalse(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.PENDING));
        assertFalse(AppointmentStatus.COMPLETED.canTransitionTo(AppointmentStatus.CONFIRMED));
        assertFalse(AppointmentStatus.CANCELLED.canTransitionTo(AppointmentStatus.PENDING));
        assertFalse(AppointmentStatus.PENDING.canTransitionTo(null));
    }
}
