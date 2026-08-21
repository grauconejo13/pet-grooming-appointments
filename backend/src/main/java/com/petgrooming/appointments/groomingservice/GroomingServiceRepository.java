package com.petgrooming.appointments.groomingservice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroomingServiceRepository extends JpaRepository<GroomingService, Long> {

    boolean existsByNameIgnoreCase(String name);
}
