package com.petgrooming.appointments.groomer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GroomerController.class)
class GroomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroomerService groomerService;

    @Test
    void listsActiveGroomers() throws Exception {
        when(groomerService.getActive()).thenReturn(List.of(
            new Groomer(7L, "Alex", "Rivera", "alex@example.test", null, true)
        ));

        mockMvc.perform(get("/api/groomers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7))
            .andExpect(jsonPath("$[0].fullName").value("Alex Rivera"))
            .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getsGroomerById() throws Exception {
        when(groomerService.get(7L))
            .thenReturn(new Groomer(7L, "Alex", "Rivera", "alex@example.test", null, true));

        mockMvc.perform(get("/api/groomers/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("alex@example.test"));
    }

    @Test
    void returnsNotFoundForMissingGroomer() throws Exception {
        when(groomerService.get(99L))
            .thenThrow(new IllegalArgumentException("groomer not found"));

        mockMvc.perform(get("/api/groomers/99"))
            .andExpect(status().isNotFound());
    }
}
