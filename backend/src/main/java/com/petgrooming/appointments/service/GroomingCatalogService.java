package com.petgrooming.appointments.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroomingCatalogService {
    private final GroomingServiceRepository repository;

    public GroomingCatalogService(GroomingServiceRepository repository) {
        this.repository = repository;
    }

    public List<GroomingService> getActive() {
        return repository.findActive();
    }

    public GroomingService get(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("grooming service not found"));
    }
}
