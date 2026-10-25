package com.petgrooming.appointments.groomer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class GroomerRepositoryTest {

    @Test
    void savesGroomerAndReturnsGeneratedId() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        GroomerRepository repository = new GroomerRepository(jdbcTemplate);
        Groomer unsaved = new Groomer(null, "Alex", "Rivera", "alex@example.test", null, true);

        when(jdbcTemplate.queryForObject(
            "INSERT INTO groomers (first_name, last_name, email, phone, active) VALUES (?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            "Alex",
            "Rivera",
            "alex@example.test",
            null,
            true
        )).thenReturn(7L);

        Groomer saved = repository.save(unsaved);

        assertEquals(7L, saved.id());
        assertEquals("Alex Rivera", saved.fullName());
    }
}
