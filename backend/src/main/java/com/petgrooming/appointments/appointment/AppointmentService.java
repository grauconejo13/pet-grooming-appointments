package com.petgrooming.appointments.appointment;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment create(Appointment appointment) {
        List<Appointment> conflicts = appointmentRepository.findByGroomerBetween(
            appointment.groomerId(),
            appointment.startsAt(),
            appointment.endsAt()
        );

        boolean hasActiveConflict = conflicts.stream()
            .anyMatch(existing -> existing.status() != AppointmentStatus.CANCELLED);

        if (hasActiveConflict) {
            throw new IllegalStateException("groomer is not available for the requested time");
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment transitionStatus(long appointmentId, AppointmentStatus nextStatus) {
        Appointment existing = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("appointment not found"));

        if (!existing.status().canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                "cannot transition appointment from " + existing.status() + " to " + nextStatus
            );
        }

        int updatedRows = appointmentRepository.updateStatus(appointmentId, nextStatus);
        if (updatedRows != 1) {
            throw new IllegalStateException("appointment status update failed");
        }

        return new Appointment(
            existing.id(),
            existing.petId(),
            existing.groomerId(),
            existing.serviceId(),
            existing.startsAt(),
            existing.endsAt(),
            nextStatus,
            existing.clientNote(),
            existing.groomerNote()
        );
    }
}
