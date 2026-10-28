package com.petgrooming.appointments.appointment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment newAppointment = new Appointment(
            null,
            request.petId(),
            request.groomerId(),
            request.serviceId(),
            request.startsAt(),
            request.endsAt(),
            AppointmentStatus.PENDING,
            request.clientNote(),
            null
        );
        Appointment saved;
        try {
            saved = appointmentService.create(newAppointment);
        } catch (IllegalStateException unavailable) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, unavailable.getMessage(), unavailable);
        }
        return ResponseEntity.created(URI.create("/api/appointments/" + saved.id())).body(saved);
    }

    public record CreateAppointmentRequest(
        @NotNull @Positive Long petId,
        @NotNull @Positive Long groomerId,
        @NotNull @Positive Long serviceId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        String clientNote
    ) {}
}
