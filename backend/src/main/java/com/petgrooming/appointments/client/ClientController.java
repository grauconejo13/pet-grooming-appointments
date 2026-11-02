package com.petgrooming.appointments.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody CreateClientRequest request) {
        Client saved = clientService.create(new Client(
            null,
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone()
        ));

        return ResponseEntity.created(URI.create("/api/clients/" + saved.id())).body(saved);
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable long id) {
        try {
            return clientService.get(id);
        } catch (IllegalArgumentException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound.getMessage(), notFound);
        }
    }

    public record CreateClientRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 30) String phone
    ) {}
}
