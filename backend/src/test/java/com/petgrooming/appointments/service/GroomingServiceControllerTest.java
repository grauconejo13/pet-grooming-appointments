package com.petgrooming.appointments.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GroomingServiceController.class)
class GroomingServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroomingCatalogService catalogService;

    @Test
    void createsGroomingService() throws Exception {
        when(catalogService.create(any(GroomingService.class))).thenReturn(
            new GroomingService(
                12L,
                "Full Groom",
                "Bath, trim, and nails",
                90,
                new BigDecimal("85.00"),
                true
            )
        );

        mockMvc.perform(
            post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Full Groom",
                      "description": "Bath, trim, and nails",
                      "durationMinutes": 90,
                      "price": 85.00
                    }
                    """)
        )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/services/12"))
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void rejectsInvalidServicePayload() throws Exception {
        mockMvc.perform(
            post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "durationMinutes": 0,
                      "price": -1
                    }
                    """)
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    void listsActiveServices() throws Exception {
        when(catalogService.getActive()).thenReturn(List.of(
            new GroomingService(
                12L,
                "Full Groom",
                "Bath, trim, and nails",
                90,
                new BigDecimal("85.00"),
                true
            )
        ));

        mockMvc.perform(get("/api/services"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Full Groom"))
            .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getsServiceById() throws Exception {
        when(catalogService.get(12L)).thenReturn(
            new GroomingService(
                12L,
                "Full Groom",
                null,
                90,
                new BigDecimal("85.00"),
                true
            )
        );

        mockMvc.perform(get("/api/services/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.durationMinutes").value(90));
    }

    @Test
    void returnsNotFoundForMissingService() throws Exception {
        when(catalogService.get(99L))
            .thenThrow(new IllegalArgumentException("grooming service not found"));

        mockMvc.perform(get("/api/services/99"))
            .andExpect(status().isNotFound());
    }
}
