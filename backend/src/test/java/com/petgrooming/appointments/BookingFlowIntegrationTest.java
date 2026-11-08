package com.petgrooming.appointments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BookingFlowIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("pet_grooming")
            .withUsername("pet_grooming")
            .withPassword("pet_grooming");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completesClientPetBookingLookupRescheduleAndCancellationFlow() throws Exception {
        JsonNode groomers = json(
            mockMvc.perform(get("/api/groomers"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        long groomerId = groomers.get(0).get("id").asLong();

        JsonNode services = json(
            mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        long serviceId = services.get(0).get("id").asLong();

        JsonNode client = json(
            mockMvc.perform(
                post("/api/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "firstName": "Jamie",
                          "lastName": "Morgan",
                          "email": "jamie.morgan@example.test",
                          "phone": "2105550199"
                        }
                        """)
            )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        long clientId = client.get("id").asLong();

        JsonNode pet = json(
            mockMvc.perform(
                post("/api/pets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "clientId": %d,
                          "name": "Mochi",
                          "species": "Dog",
                          "breed": "Pug",
                          "size": "Small"
                        }
                        """.formatted(clientId))
            )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        long petId = pet.get("id").asLong();

        JsonNode appointment = json(
            mockMvc.perform(
                post("/api/appointments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "petId": %d,
                          "groomerId": %d,
                          "serviceId": %d,
                          "startsAt": "2030-06-15T10:00:00",
                          "endsAt": "2030-06-15T11:00:00",
                          "clientNote": "Sensitive paws"
                        }
                        """.formatted(petId, groomerId, serviceId))
            )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        long appointmentId = appointment.get("id").asLong();

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.petId").value(petId))
            .andExpect(jsonPath("$.clientNote").value("Sensitive paws"));

        mockMvc.perform(
            patch("/api/appointments/{id}/schedule", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "startsAt": "2030-06-16T13:00:00",
                      "endsAt": "2030-06-16T14:00:00"
                    }
                    """)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startsAt").value("2030-06-16T13:00:00"));

        mockMvc.perform(
            patch("/api/appointments/{id}/status", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"))
            .andExpect(jsonPath("$.startsAt").value("2030-06-16T13:00:00"));
    }

    private JsonNode json(String content) throws Exception {
        return objectMapper.readTree(content);
    }
}
