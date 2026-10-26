package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
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

    private Appointment appointment(Long id, AppointmentStatus status) {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 26, 9, 0);
        return new Appointment(
            id, 10L, 20L, 30L, startsAt, startsAt.plusMinutes(60), status, null, null
        );
    }
}
