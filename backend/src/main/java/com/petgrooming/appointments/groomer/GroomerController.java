package com.petgrooming.appointments.groomer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/groomers")
public class GroomerController {
    private final GroomerService groomerService;

    public GroomerController(GroomerService groomerService) {
        this.groomerService = groomerService;
    }

    @PostMapping
    public ResponseEntity<Groomer> create(@Valid @RequestBody CreateGroomerRequest request) {
        Groomer saved = groomerService.create(new Groomer(
            null,
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            true
        ));
        return ResponseEntity.created(URI.create("/api/groomers/" + saved.id())).body(saved);
    }

    @GetMapping
    public List<Groomer> getActive() {
        return groomerService.getActive();
    }

    @GetMapping("/{id}")
    public Groomer getById(@PathVariable long id) {
        try {
            return groomerService.get(id);
        } catch (IllegalArgumentException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound.getMessage(), notFound);
        }
    }

    public record CreateGroomerRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 30) String phone
    ) {}
}
