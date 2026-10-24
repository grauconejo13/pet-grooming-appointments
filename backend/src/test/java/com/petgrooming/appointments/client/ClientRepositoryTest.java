package com.petgrooming.appointments.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class ClientRepositoryTest {

    @Test
    void savesClientAndReturnsGeneratedId() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        ClientRepository repository = new ClientRepository(jdbcTemplate);
        Client unsaved = new Client(null, "Jordan", "Lee", "jordan@example.test", "5550100");

        when(jdbcTemplate.queryForObject(
            "INSERT INTO clients (first_name, last_name, email, phone) VALUES (?, ?, ?, ?) RETURNING id",
            Long.class,
            "Jordan",
            "Lee",
            "jordan@example.test",
            "5550100"
        )).thenReturn(42L);

        Client saved = repository.save(unsaved);

        assertEquals(42L, saved.id());
        assertEquals("Jordan Lee", saved.fullName());
        verify(jdbcTemplate).queryForObject(
            "INSERT INTO clients (first_name, last_name, email, phone) VALUES (?, ?, ?, ?) RETURNING id",
            Long.class,
            "Jordan",
            "Lee",
            "jordan@example.test",
            "5550100"
        );
    }
}
