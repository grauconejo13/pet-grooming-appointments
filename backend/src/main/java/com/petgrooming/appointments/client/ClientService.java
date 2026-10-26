package com.petgrooming.appointments.client;

import java.util.List;
import org.springframework.stereotype.Service;
import com.petgrooming.appointments.pet.Pet;
import com.petgrooming.appointments.pet.PetRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PetRepository petRepository;

    public ClientService(ClientRepository clientRepository, PetRepository petRepository) {
        this.clientRepository = clientRepository;
        this.petRepository = petRepository;
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public Client get(long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("client not found"));
    }

    public List<Pet> getPets(long clientId) {
        get(clientId);
        return petRepository.findByClientId(clientId);
    }
}
