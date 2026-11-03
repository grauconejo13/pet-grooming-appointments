package com.petgrooming.appointments.pet;

import java.util.List;
import org.springframework.stereotype.Service;
import com.petgrooming.appointments.client.ClientRepository;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;

    public PetService(PetRepository petRepository, ClientRepository clientRepository) {
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
    }

    public Pet create(Pet pet) {
        requireClient(pet.clientId());
        return petRepository.save(pet);
    }

    public Pet get(long id) {
        return petRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("pet not found"));
    }

    public List<Pet> getByClient(long clientId) {
        requireClient(clientId);
        return petRepository.findByClientId(clientId);
    }

    private void requireClient(long clientId) {
        clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("client not found"));
    }
}
