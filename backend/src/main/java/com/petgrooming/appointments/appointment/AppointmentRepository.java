package com.petgrooming.appointments.appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Appointment save(Appointment appointment) {
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO appointments (pet_id, groomer_id, service_id, starts_at, ends_at, status, client_note, groomer_note) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
            Long.class,
            appointment.petId(),
            appointment.groomerId(),
            appointment.serviceId(),
            appointment.startsAt(),
            appointment.endsAt(),
            appointment.status().name(),
            appointment.clientNote(),
            appointment.groomerNote()
        );

        return new Appointment(
            id,
            appointment.petId(),
            appointment.groomerId(),
            appointment.serviceId(),
            appointment.startsAt(),
            appointment.endsAt(),
            appointment.status(),
            appointment.clientNote(),
            appointment.groomerNote()
        );
    }

    public Optional<Appointment> findById(long id) {
        List<Appointment> appointments = jdbcTemplate.query(
            "SELECT id, pet_id, groomer_id, service_id, starts_at, ends_at, status, client_note, groomer_note FROM appointments WHERE id = ?",
            this::mapAppointment,
            id
        );
        return appointments.stream().findFirst();
    }

    public int updateStatus(long id, AppointmentStatus status) {
        return jdbcTemplate.update(
            "UPDATE appointments SET status = ? WHERE id = ?",
            status.name(),
            id
        );
    }

    public List<Appointment> findByGroomerBetween(
        long groomerId,
        LocalDateTime windowStart,
        LocalDateTime windowEnd
    ) {
        return jdbcTemplate.query(
            "SELECT id, pet_id, groomer_id, service_id, starts_at, ends_at, status, client_note, groomer_note " +
                "FROM appointments WHERE groomer_id = ? AND starts_at < ? AND ends_at > ? ORDER BY starts_at",
            this::mapAppointment,
            groomerId,
            windowEnd,
            windowStart
        );
    }

    private Appointment mapAppointment(ResultSet rs, int rowNum) throws SQLException {
        return new Appointment(
            rs.getLong("id"),
            rs.getLong("pet_id"),
            rs.getLong("groomer_id"),
            rs.getLong("service_id"),
            rs.getObject("starts_at", LocalDateTime.class),
            rs.getObject("ends_at", LocalDateTime.class),
            AppointmentStatus.valueOf(rs.getString("status")),
            rs.getString("client_note"),
            rs.getString("groomer_note")
        );
    }
}
