package com.petgrooming.appointments.groomer;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/groomers")
public class GroomerController {
    private final GroomerService groomerService;

    public GroomerController(GroomerService groomerService) {
        this.groomerService = groomerService;
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
}
