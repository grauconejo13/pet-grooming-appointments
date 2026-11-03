package com.petgrooming.appointments.pet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
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
@RequestMapping("/api")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/pets")
    public ResponseEntity<Pet> create(@Valid @RequestBody CreatePetRequest request) {
        try {
            Pet saved = petService.create(new Pet(
                null,
                request.clientId(),
                request.name(),
                request.species(),
                request.breed(),
                request.birthDate(),
                request.size(),
                request.careNotes()
            ));
            return ResponseEntity.created(URI.create("/api/pets/" + saved.id())).body(saved);
        } catch (IllegalArgumentException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound.getMessage(), notFound);
        }
    }

    @GetMapping("/clients/{clientId}/pets")
    public List<Pet> getByClient(@PathVariable @Positive long clientId) {
        try {
            return petService.getByClient(clientId);
        } catch (IllegalArgumentException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound.getMessage(), notFound);
        }
    }

    public record CreatePetRequest(
        @NotNull @Positive Long clientId,
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 40) String species,
        @Size(max = 80) String breed,
        LocalDate birthDate,
        @NotBlank @Size(max = 20) String size,
        String careNotes
    ) {}
}
