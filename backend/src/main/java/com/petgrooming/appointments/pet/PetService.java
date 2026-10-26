package com.petgrooming.appointments.pet;

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
        clientRepository.findById(pet.clientId())
            .orElseThrow(() -> new IllegalArgumentException("client not found"));
        return petRepository.save(pet);
    }

    public Pet get(long id) {
        return petRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("pet not found"));
    }
}
