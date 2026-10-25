package com.petgrooming.appointments.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GroomingServiceRepository {
    private final JdbcTemplate jdbcTemplate;

    public GroomingServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public GroomingService save(GroomingService service) {
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO grooming_services (name, description, duration_minutes, price, active) VALUES (?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            service.name(),
            service.description(),
            service.durationMinutes(),
            service.price(),
            service.active()
        );

        return new GroomingService(
            id,
            service.name(),
            service.description(),
            service.durationMinutes(),
            service.price(),
            service.active()
        );
    }

    public Optional<GroomingService> findById(long id) {
        List<GroomingService> services = jdbcTemplate.query(
            "SELECT id, name, description, duration_minutes, price, active FROM grooming_services WHERE id = ?",
            this::mapService,
            id
        );
        return services.stream().findFirst();
    }

    public List<GroomingService> findActive() {
        return jdbcTemplate.query(
            "SELECT id, name, description, duration_minutes, price, active FROM grooming_services WHERE active = TRUE ORDER BY name",
            this::mapService
        );
    }

    private GroomingService mapService(ResultSet rs, int rowNum) throws SQLException {
        return new GroomingService(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getInt("duration_minutes"),
            rs.getBigDecimal("price"),
            rs.getBoolean("active")
        );
    }
}
