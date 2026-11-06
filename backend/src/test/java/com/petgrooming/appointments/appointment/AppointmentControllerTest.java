package com.petgrooming.appointments.appointment;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    void listsAppointmentsForGroomerWindow() throws Exception {
        LocalDateTime from = LocalDateTime.of(2026, 11, 3, 9, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 3, 17, 0);
        Appointment appointment = new Appointment(
            42L, 10L, 20L, 30L, from.plusHours(1), from.plusHours(2),
            AppointmentStatus.CONFIRMED, null, null
        );

        when(appointmentService.findByGroomerBetween(20L, from, to))
            .thenReturn(List.of(appointment));

        mockMvc.perform(
            get("/api/appointments")
                .param("groomerId", "20")
                .param("from", "2026-11-03T09:00:00")
                .param("to", "2026-11-03T17:00:00")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(42))
            .andExpect(jsonPath("$[0].groomerId").value(20));
    }

    @Test
    void reschedulesAppointment() throws Exception {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 8, 13, 0);
        LocalDateTime endsAt = startsAt.plusMinutes(90);
        Appointment updated = new Appointment(
            42L, 10L, 20L, 30L, startsAt, endsAt,
            AppointmentStatus.CONFIRMED, null, null
        );

        when(appointmentService.reschedule(42L, startsAt, endsAt)).thenReturn(updated);

        mockMvc.perform(
            patch("/api/appointments/42/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "startsAt": "2026-11-08T13:00:00",
                      "endsAt": "2026-11-08T14:30:00"
                    }
                    """)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startsAt").value("2026-11-08T13:00:00"));
    }

    @Test
    void updatesAppointmentStatus() throws Exception {
        LocalDateTime startsAt = LocalDateTime.of(2026, 10, 30, 10, 0);
        Appointment confirmed = new Appointment(
            42L, 10L, 20L, 30L, startsAt, startsAt.plusMinutes(60),
            AppointmentStatus.CONFIRMED, null, null
        );

        when(appointmentService.transitionStatus(42L, AppointmentStatus.CONFIRMED))
            .thenReturn(confirmed);

        mockMvc.perform(
            patch("/api/appointments/42/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void returnsConflictForInvalidTransition() throws Exception {
        when(appointmentService.transitionStatus(42L, AppointmentStatus.PENDING))
            .thenThrow(new IllegalStateException("cannot transition appointment"));

        mockMvc.perform(
            patch("/api/appointments/42/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PENDING\"}")
        )
            .andExpect(status().isConflict());
    }

    @Test
    void returnsNotFoundForMissingAppointment() throws Exception {
        when(appointmentService.transitionStatus(99L, AppointmentStatus.CANCELLED))
            .thenThrow(new IllegalArgumentException("appointment not found"));

        mockMvc.perform(
            patch("/api/appointments/99/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}")
        )
            .andExpect(status().isNotFound());
    }
}
