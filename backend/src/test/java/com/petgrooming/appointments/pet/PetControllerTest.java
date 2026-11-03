package com.petgrooming.appointments.pet;

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

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Test
    void createsPet() throws Exception {
        when(petService.create(any(Pet.class)))
            .thenReturn(new Pet(7L, 42L, "Mochi", "Dog", null, null, "Small", null));

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientId": 42,
                      "name": "Mochi",
                      "species": "Dog",
                      "size": "Small"
                    }
                    """)
        )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/pets/7"))
            .andExpect(jsonPath("$.name").value("Mochi"));
    }

    @Test
    void listsPetsForClient() throws Exception {
        when(petService.getByClient(42L))
            .thenReturn(List.of(new Pet(7L, 42L, "Mochi", "Dog", null, null, "Small", null)));

        mockMvc.perform(get("/api/clients/42/pets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Mochi"));
    }

    @Test
    void returnsNotFoundWhenClientDoesNotExist() throws Exception {
        when(petService.getByClient(99L)).thenThrow(new IllegalArgumentException("client not found"));

        mockMvc.perform(get("/api/clients/99/pets"))
            .andExpect(status().isNotFound());
    }
}
