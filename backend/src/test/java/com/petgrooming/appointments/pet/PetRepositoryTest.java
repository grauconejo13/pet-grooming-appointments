package com.petgrooming.appointments.pet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class PetRepositoryTest {

    @Test
    void savesPetAndReturnsGeneratedId() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PetRepository repository = new PetRepository(jdbcTemplate);
        LocalDate birthDate = LocalDate.of(2022, 4, 12);
        Pet unsaved = new Pet(null, 10L, "Mochi", "Dog", "Shih Tzu", birthDate, "Small", null);

        when(jdbcTemplate.queryForObject(
            "INSERT INTO pets (client_id, name, species, breed, birth_date, size, care_notes) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            10L,
            "Mochi",
            "Dog",
            "Shih Tzu",
            birthDate,
            "Small",
            null
        )).thenReturn(99L);

        Pet saved = repository.save(unsaved);

        assertEquals(99L, saved.id());
        assertEquals(10L, saved.clientId());
        assertEquals("Mochi", saved.name());
    }
}
