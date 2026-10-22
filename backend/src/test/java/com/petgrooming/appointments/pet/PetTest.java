package com.petgrooming.appointments.pet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PetTest {

    @Test
    void createsPetAndNormalizesOptionalText() {
        Pet pet = new Pet(
            1L,
            10L,
            "  Mochi ",
            " Dog ",
            " ",
            LocalDate.of(2022, 4, 12),
            " Small ",
            "  Sensitive paws  "
        );

        assertEquals("Mochi", pet.name());
        assertEquals("Dog", pet.species());
        assertEquals("Small", pet.size());
        assertNull(pet.breed());
        assertEquals("Sensitive paws", pet.careNotes());
    }

    @Test
    void rejectsMissingClientReference() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pet(null, null, "Mochi", "Dog", null, null, "Small", null)
        );
    }
}
