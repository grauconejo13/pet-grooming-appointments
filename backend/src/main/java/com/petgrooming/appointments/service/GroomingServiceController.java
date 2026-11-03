package com.petgrooming.appointments.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
