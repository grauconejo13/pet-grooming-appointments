package com.petgrooming.appointments.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public Optional<Appointment> findById(long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> findByGroomerBetween(
        long groomerId,
        LocalDateTime from,
        LocalDateTime to
    ) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new IllegalArgumentException("to must be after from");
        }
        return appointmentRepository.findByGroomerBetween(groomerId, from, to);
    }

    public Appointment reschedule(long appointmentId, LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt == null || endsAt == null || !endsAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }

        Appointment existing = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("appointment not found"));

        if (existing.status() == AppointmentStatus.CANCELLED || existing.status() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("completed or cancelled appointments cannot be rescheduled");
        }

        boolean hasActiveConflict = appointmentRepository.findByGroomerBetween(
                existing.groomerId(),
                startsAt,
                endsAt
            ).stream()
            .anyMatch(other ->
                other.id() != appointmentId &&
                other.status() != AppointmentStatus.CANCELLED
            );

        if (hasActiveConflict) {
            throw new IllegalStateException("groomer is not available for the requested time");
        }

        int updatedRows = appointmentRepository.updateSchedule(appointmentId, startsAt, endsAt);
        if (updatedRows != 1) {
            throw new IllegalStateException("appointment schedule update failed");
        }

        return new Appointment(
            existing.id(),
            existing.petId(),
            existing.groomerId(),
            existing.serviceId(),
            startsAt,
            endsAt,
            existing.status(),
            existing.clientNote(),
            existing.groomerNote()
        );
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
