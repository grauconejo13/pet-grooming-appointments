package com.petgrooming.appointments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class GroomingServiceRepositoryTest {

    @Test
    void savesServiceAndReturnsGeneratedId() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        GroomingServiceRepository repository = new GroomingServiceRepository(jdbcTemplate);
        GroomingService unsaved = new GroomingService(
            null,
            "Full Groom",
            "Bath, trim, and nails",
            90,
            new BigDecimal("85.00"),
            true
        );

        when(jdbcTemplate.queryForObject(
            "INSERT INTO grooming_services (name, description, duration_minutes, price, active) VALUES (?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            "Full Groom",
            "Bath, trim, and nails",
            90,
            new BigDecimal("85.00"),
            true
        )).thenReturn(12L);

        GroomingService saved = repository.save(unsaved);

        assertEquals(12L, saved.id());
        assertEquals(new BigDecimal("85.00"), saved.price());
    }
}
