package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AppointmentServiceTest {

    private final AppointmentRepository repository = mock(AppointmentRepository.class);
    private final AppointmentService service = new AppointmentService(repository);

    @Test
    void createsAppointmentWhenGroomerIsAvailable() {
        Appointment appointment = appointment(null, AppointmentStatus.PENDING);
        Appointment saved = appointment(42L, AppointmentStatus.PENDING);

        when(repository.findByGroomerBetween(20L, appointment.startsAt(), appointment.endsAt()))
            .thenReturn(List.of());
        when(repository.save(appointment)).thenReturn(saved);

        assertEquals(42L, service.create(appointment).id());
    }

    @Test
    void rejectsOverlappingActiveAppointment() {
        Appointment appointment = appointment(null, AppointmentStatus.PENDING);
        when(repository.findByGroomerBetween(20L, appointment.startsAt(), appointment.endsAt()))
            .thenReturn(List.of(appointment(7L, AppointmentStatus.CONFIRMED)));

        assertThrows(IllegalStateException.class, () -> service.create(appointment));
        verify(repository, never()).save(appointment);
    }

    @Test
    void ignoresCancelledAppointmentsWhenCheckingAvailability() {
        Appointment appointment = appointment(null, AppointmentStatus.PENDING);
        when(repository.findByGroomerBetween(20L, appointment.startsAt(), appointment.endsAt()))
            .thenReturn(List.of(appointment(7L, AppointmentStatus.CANCELLED)));
        when(repository.save(appointment)).thenReturn(appointment(42L, AppointmentStatus.PENDING));

        service.create(appointment);

        verify(repository).save(appointment);
    }

    @Test
    void findsAppointmentById() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        when(repository.findById(42L)).thenReturn(Optional.of(existing));

        Optional<Appointment> result = service.findById(42L);

        assertTrue(result.isPresent());
        assertEquals(42L, result.orElseThrow().id());
    }

    @Test
    void reschedulesAvailableAppointment() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        LocalDateTime newStart = existing.startsAt().plusDays(2).plusHours(3);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(existing));
        when(repository.findByGroomerBetween(20L, newStart, newEnd)).thenReturn(List.of());
        when(repository.updateSchedule(42L, newStart, newEnd)).thenReturn(1);

        Appointment updated = service.reschedule(42L, newStart, newEnd);

        assertEquals(newStart, updated.startsAt());
        assertEquals(newEnd, updated.endsAt());
        verify(repository).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void ignoresCurrentAppointmentDuringRescheduleConflictCheck() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        LocalDateTime newStart = existing.startsAt().plusHours(2);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(existing));
        when(repository.findByGroomerBetween(20L, newStart, newEnd))
            .thenReturn(List.of(existing));
        when(repository.updateSchedule(42L, newStart, newEnd)).thenReturn(1);

        service.reschedule(42L, newStart, newEnd);

        verify(repository).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void rejectsRescheduleWhenAnotherActiveAppointmentConflicts() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        Appointment conflict = appointment(77L, AppointmentStatus.PENDING);
        LocalDateTime newStart = existing.startsAt().plusHours(2);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(existing));
        when(repository.findByGroomerBetween(20L, newStart, newEnd))
            .thenReturn(List.of(conflict));

        assertThrows(
            IllegalStateException.class,
            () -> service.reschedule(42L, newStart, newEnd)
        );
        verify(repository, never()).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void ignoresCancelledAppointmentDuringRescheduleConflictCheck() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        Appointment cancelled = appointment(77L, AppointmentStatus.CANCELLED);
        LocalDateTime newStart = existing.startsAt().plusHours(2);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(existing));
        when(repository.findByGroomerBetween(20L, newStart, newEnd))
            .thenReturn(List.of(cancelled));
        when(repository.updateSchedule(42L, newStart, newEnd)).thenReturn(1);

        service.reschedule(42L, newStart, newEnd);

        verify(repository).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void rejectsReschedulingCancelledAppointment() {
        Appointment cancelled = appointment(42L, AppointmentStatus.CANCELLED);
        LocalDateTime newStart = cancelled.startsAt().plusDays(1);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(cancelled));

        assertThrows(
            IllegalStateException.class,
            () -> service.reschedule(42L, newStart, newEnd)
        );
        verify(repository, never()).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void rejectsReschedulingCompletedAppointment() {
        Appointment completed = appointment(42L, AppointmentStatus.COMPLETED);
        LocalDateTime newStart = completed.startsAt().plusDays(1);
        LocalDateTime newEnd = newStart.plusMinutes(60);

        when(repository.findById(42L)).thenReturn(Optional.of(completed));

        assertThrows(
            IllegalStateException.class,
            () -> service.reschedule(42L, newStart, newEnd)
        );
        verify(repository, never()).updateSchedule(42L, newStart, newEnd);
    }

    @Test
    void rejectsInvalidRescheduleWindow() {
        Appointment existing = appointment(42L, AppointmentStatus.CONFIRMED);
        LocalDateTime newStart = existing.startsAt().plusDays(1);

        assertThrows(
            IllegalArgumentException.class,
            () -> service.reschedule(42L, newStart, newStart)
        );

        verify(repository, never()).findById(42L);
        verify(repository, never()).updateSchedule(42L, newStart, newStart);
    }

    private Appointment appointment(Long id, AppointmentStatus status) {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 26, 9, 0);
        return new Appointment(
            id, 10L, 20L, 30L, startsAt, startsAt.plusMinutes(60), status, null, null
        );
    }
}
