package com.petgrooming.appointments.groomer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GroomerRepository {
    private final JdbcTemplate jdbcTemplate;

    public GroomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Groomer save(Groomer groomer) {
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO groomers (first_name, last_name, email, phone, active) VALUES (?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            groomer.firstName(),
            groomer.lastName(),
            groomer.email(),
            groomer.phone(),
            groomer.active()
        );

        return new Groomer(
            id,
            groomer.firstName(),
            groomer.lastName(),
            groomer.email(),
            groomer.phone(),
            groomer.active()
        );
    }

    public Optional<Groomer> findById(long id) {
        List<Groomer> groomers = jdbcTemplate.query(
            "SELECT id, first_name, last_name, email, phone, active FROM groomers WHERE id = ?",
            this::mapGroomer,
            id
        );
        return groomers.stream().findFirst();
    }

    public List<Groomer> findActive() {
        return jdbcTemplate.query(
            "SELECT id, first_name, last_name, email, phone, active FROM groomers WHERE active = TRUE ORDER BY last_name, first_name",
            this::mapGroomer
        );
    }

    private Groomer mapGroomer(ResultSet rs, int rowNum) throws SQLException {
        return new Groomer(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getBoolean("active")
        );
    }
}
