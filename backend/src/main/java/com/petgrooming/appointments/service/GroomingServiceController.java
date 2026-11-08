package com.petgrooming.appointments.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.net.URI;
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
@RequestMapping("/api/services")
public class GroomingServiceController {
    private final GroomingCatalogService catalogService;

    public GroomingServiceController(GroomingCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping
    public ResponseEntity<GroomingService> create(@Valid @RequestBody CreateServiceRequest request) {
        GroomingService saved = catalogService.create(new GroomingService(
            null,
            request.name(),
            request.description(),
            request.durationMinutes(),
            request.price(),
            true
        ));
        return ResponseEntity.created(URI.create("/api/services/" + saved.id())).body(saved);
    }

    @GetMapping
    public List<GroomingService> getActive() {
        return catalogService.getActive();
    }

    @GetMapping("/{id}")
    public GroomingService getById(@PathVariable long id) {
        try {
            return catalogService.get(id);
        } catch (IllegalArgumentException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound.getMessage(), notFound);
        }
    }

    public record CreateServiceRequest(
        @NotBlank @Size(max = 100) String name,
        String description,
        @NotNull @Positive Integer durationMinutes,
        @NotNull @DecimalMin(value = "0.00") BigDecimal price
    ) {}
}
