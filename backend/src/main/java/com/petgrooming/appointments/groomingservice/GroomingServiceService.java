package com.petgrooming.appointments.groomingservice;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class GroomingServiceService {

    private final GroomingServiceRepository groomingServiceRepository;

    public GroomingServiceService(GroomingServiceRepository groomingServiceRepository) {
        this.groomingServiceRepository = groomingServiceRepository;
    }

    @Transactional
    public GroomingServiceResponse create(CreateGroomingServiceRequest request) {
        String name = request.name().trim();
        if (groomingServiceRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A grooming service with that name already exists.");
        }

        GroomingService groomingService = new GroomingService(
                name,
                normalizeDescription(request.description()),
                request.durationMinutes(),
                request.price()
        );

        return GroomingServiceResponse.from(groomingServiceRepository.save(groomingService));
    }

    public List<GroomingServiceResponse> findAll() {
        return groomingServiceRepository.findAll().stream()
                .map(GroomingServiceResponse::from)
                .toList();
    }

    public GroomingServiceResponse findById(Long id) {
        return groomingServiceRepository.findById(id)
                .map(GroomingServiceResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grooming service not found."));
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
