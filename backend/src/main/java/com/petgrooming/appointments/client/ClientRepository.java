package com.petgrooming.appointments.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Client save(Client client) {
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO clients (first_name, last_name, email, phone) VALUES (?, ?, ?, ?) RETURNING id",
            Long.class,
            client.firstName(),
            client.lastName(),
            client.email(),
            client.phone()
        );

        return new Client(id, client.firstName(), client.lastName(), client.email(), client.phone());
    }

    public Optional<Client> findById(long id) {
        List<Client> clients = jdbcTemplate.query(
            "SELECT id, first_name, last_name, email, phone FROM clients WHERE id = ?",
            this::mapClient,
            id
        );
        return clients.stream().findFirst();
    }

    private Client mapClient(ResultSet rs, int rowNum) throws SQLException {
        return new Client(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
