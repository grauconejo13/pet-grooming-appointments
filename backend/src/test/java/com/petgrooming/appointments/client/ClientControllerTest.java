package com.petgrooming.appointments.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    void createsClient() throws Exception {
        when(clientService.create(any(Client.class)))
            .thenReturn(new Client(42L, "Jordan", "Lee", "jordan@example.test", "5550100"));

        mockMvc.perform(
            post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "Jordan",
                      "lastName": "Lee",
                      "email": "jordan@example.test",
                      "phone": "5550100"
                    }
                    """)
        )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/clients/42"))
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.firstName").value("Jordan"));
    }

    @Test
    void getsClientById() throws Exception {
        when(clientService.get(42L))
            .thenReturn(new Client(42L, "Jordan", "Lee", "jordan@example.test", "5550100"));

        mockMvc.perform(get("/api/clients/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("jordan@example.test"));
    }

    @Test
    void returnsNotFoundForMissingClient() throws Exception {
        when(clientService.get(99L)).thenThrow(new IllegalArgumentException("client not found"));

        mockMvc.perform(get("/api/clients/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidClientPayload() throws Exception {
        mockMvc.perform(
            post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "",
                      "lastName": "Lee",
                      "email": "not-an-email",
                      "phone": ""
                    }
                    """)
        )
            .andExpect(status().isBadRequest());
    }
}
