package com.petgrooming.appointments.groomingservice;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class GroomingServiceController {

    private final GroomingServiceService groomingServiceService;

    public GroomingServiceController(GroomingServiceService groomingServiceService) {
        this.groomingServiceService = groomingServiceService;
    }

    @PostMapping
    public ResponseEntity<GroomingServiceResponse> create(@Valid @RequestBody CreateGroomingServiceRequest request) {
        GroomingServiceResponse response = groomingServiceService.create(request);
        return ResponseEntity.created(URI.create("/api/services/" + response.id())).body(response);
    }

    @GetMapping
    public List<GroomingServiceResponse> findAll() {
        return groomingServiceService.findAll();
    }

    @GetMapping("/{id}")
    public GroomingServiceResponse findById(@PathVariable Long id) {
        return groomingServiceService.findById(id);
    }
}
