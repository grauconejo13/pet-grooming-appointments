package com.petgrooming.appointments.pet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PetRepository {
    private final JdbcTemplate jdbcTemplate;

    public PetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Pet save(Pet pet) {
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO pets (client_id, name, species, breed, birth_date, size, care_notes) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            pet.clientId(),
            pet.name(),
            pet.species(),
            pet.breed(),
            pet.birthDate(),
            pet.size(),
            pet.careNotes()
        );

        return new Pet(
            id,
            pet.clientId(),
            pet.name(),
            pet.species(),
            pet.breed(),
            pet.birthDate(),
            pet.size(),
            pet.careNotes()
        );
    }

    public List<Pet> findByClientId(long clientId) {
        return jdbcTemplate.query(
            "SELECT id, client_id, name, species, breed, birth_date, size, care_notes FROM pets WHERE client_id = ? ORDER BY name",
            this::mapPet,
            clientId
        );
    }

    private Pet mapPet(ResultSet rs, int rowNum) throws SQLException {
        return new Pet(
            rs.getLong("id"),
            rs.getLong("client_id"),
            rs.getString("name"),
            rs.getString("species"),
            rs.getString("breed"),
            rs.getObject("birth_date", java.time.LocalDate.class),
            rs.getString("size"),
            rs.getString("care_notes")
        );
    }
}
