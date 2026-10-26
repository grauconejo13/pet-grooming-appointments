package com.petgrooming.appointments.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.petgrooming.appointments.pet.Pet;
import com.petgrooming.appointments.pet.PetRepository;
import com.petgrooming.appointments.pet.PetService;

class ClientPetServiceTest {

    @Test
    void returnsPetsForExistingClient() {
        ClientRepository clients = mock(ClientRepository.class);
        PetRepository pets = mock(PetRepository.class);
        ClientService service = new ClientService(clients, pets);
        Client client = new Client(1L, "Jordan", "Lee", "jordan@example.test", "5550100");
        Pet pet = new Pet(2L, 1L, "Mochi", "Dog", null, LocalDate.of(2022, 4, 12), "Small", null);

        when(clients.findById(1L)).thenReturn(Optional.of(client));
        when(pets.findByClientId(1L)).thenReturn(List.of(pet));

        assertEquals(List.of(pet), service.getPets(1L));
    }

    @Test
    void rejectsPetCreationForMissingClient() {
        ClientRepository clients = mock(ClientRepository.class);
        PetRepository pets = mock(PetRepository.class);
        PetService service = new PetService(pets, clients);
        Pet pet = new Pet(null, 99L, "Mochi", "Dog", null, null, "Small", null);

        when(clients.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(pet));
    }
}
