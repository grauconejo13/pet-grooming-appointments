package com.petgrooming.appointments.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AppointmentStatusPersistenceTest {

    @Test
    void persistsAllowedStatusTransition() {
        AppointmentRepository repository = mock(AppointmentRepository.class);
        AppointmentService service = new AppointmentService(repository);
        Appointment existing = appointment(8L, AppointmentStatus.PENDING);

        when(repository.findById(8L)).thenReturn(Optional.of(existing));
        when(repository.updateStatus(8L, AppointmentStatus.CONFIRMED)).thenReturn(1);

        Appointment updated = service.transitionStatus(8L, AppointmentStatus.CONFIRMED);

        assertEquals(AppointmentStatus.CONFIRMED, updated.status());
        verify(repository).updateStatus(8L, AppointmentStatus.CONFIRMED);
    }

    @Test
    void rejectsInvalidStatusTransitionWithoutWriting() {
        AppointmentRepository repository = mock(AppointmentRepository.class);
        AppointmentService service = new AppointmentService(repository);
        when(repository.findById(8L)).thenReturn(Optional.of(appointment(8L, AppointmentStatus.COMPLETED)));

        assertThrows(
            IllegalStateException.class,
            () -> service.transitionStatus(8L, AppointmentStatus.PENDING)
        );
        verify(repository, never()).updateStatus(8L, AppointmentStatus.PENDING);
    }

    private Appointment appointment(Long id, AppointmentStatus status) {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 26, 9, 0);
        return new Appointment(id, 10L, 20L, 30L, startsAt, startsAt.plusMinutes(60), status, null, null);
    }
}
