package com.petgrooming.appointments.groomer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GroomerController.class)
class GroomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroomerService groomerService;

    @Test
    void createsGroomer() throws Exception {
        when(groomerService.create(any(Groomer.class)))
            .thenReturn(new Groomer(7L, "Alex", "Rivera", "alex@example.test", "5550102", true));

        mockMvc.perform(
            post("/api/groomers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "Alex",
                      "lastName": "Rivera",
                      "email": "alex@example.test",
                      "phone": "5550102"
                    }
                    """)
        )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/groomers/7"))
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void rejectsInvalidGroomerPayload() throws Exception {
        mockMvc.perform(
            post("/api/groomers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "",
                      "lastName": "Rivera",
                      "email": "not-an-email"
                    }
                    """)
        )
            .andExpect(status().isBadRequest());
    }

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
