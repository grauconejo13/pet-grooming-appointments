package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class AppointmentRepositoryTest {

    @Test
    void savesAppointmentAndReturnsGeneratedId() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        AppointmentRepository repository = new AppointmentRepository(jdbcTemplate);
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 25, 9, 0);
        LocalDateTime endsAt = startsAt.plusMinutes(60);
        Appointment unsaved = new Appointment(
            null,
            10L,
            20L,
            30L,
            startsAt,
            endsAt,
            AppointmentStatus.CONFIRMED,
            "Use hypoallergenic shampoo",
            null
        );

        when(jdbcTemplate.queryForObject(
            "INSERT INTO appointments (pet_id, groomer_id, service_id, starts_at, ends_at, status, client_note, groomer_note) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            10L,
            20L,
            30L,
            startsAt,
            endsAt,
            "CONFIRMED",
            "Use hypoallergenic shampoo",
            null
        )).thenReturn(55L);

        Appointment saved = repository.save(unsaved);

        assertEquals(55L, saved.id());
        assertEquals(AppointmentStatus.CONFIRMED, saved.status());
        assertEquals(startsAt, saved.startsAt());
    }
}
