package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AppointmentTest {

    @Test
    void createsAppointmentAndNormalizesNotes() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 24, 10, 0);
        LocalDateTime endsAt = startsAt.plusMinutes(60);

        Appointment appointment = new Appointment(
            1L,
            10L,
            20L,
            30L,
            startsAt,
            endsAt,
            AppointmentStatus.CONFIRMED,
            "  Use hypoallergenic shampoo  ",
            " "
        );

        assertEquals(startsAt, appointment.startsAt());
        assertEquals(endsAt, appointment.endsAt());
        assertEquals("Use hypoallergenic shampoo", appointment.clientNote());
        assertNull(appointment.groomerNote());
    }

    @Test
    void rejectsEndTimeThatIsNotAfterStartTime() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 24, 10, 0);

        assertThrows(
            IllegalArgumentException.class,
            () -> new Appointment(
                null,
                10L,
                20L,
                30L,
                startsAt,
                startsAt,
                AppointmentStatus.PENDING,
                null,
                null
            )
        );
    }

    @Test
    void rejectsMissingRelationshipIds() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 24, 10, 0);

        assertThrows(
            IllegalArgumentException.class,
            () -> new Appointment(
                null,
                null,
                20L,
                30L,
                startsAt,
                startsAt.plusMinutes(30),
                AppointmentStatus.PENDING,
                null,
                null
            )
        );
    }
}
