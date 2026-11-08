package com.petgrooming.appointments.groomer;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroomerService {
    private final GroomerRepository repository;

    public GroomerService(GroomerRepository repository) {
        this.repository = repository;
    }

    public Groomer create(Groomer groomer) {
        return repository.save(groomer);
    }

    public List<Groomer> getActive() {
        return repository.findActive();
    }

    public Groomer get(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("groomer not found"));
    }
}
