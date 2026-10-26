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
}
