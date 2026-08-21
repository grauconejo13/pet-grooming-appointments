package com.petgrooming.appointments.groomingservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class GroomingServiceServiceTest {

    @Mock
    private GroomingServiceRepository groomingServiceRepository;

    @InjectMocks
    private GroomingServiceService groomingServiceService;

    @Test
    void createsAnActiveServiceAndTrimsText() {
        CreateGroomingServiceRequest request = new CreateGroomingServiceRequest(
                "  Full Groom  ", "  Includes bath and trim.  ", 90, new BigDecimal("65.00"));
        GroomingService saved = new GroomingService("Full Groom", "Includes bath and trim.", 90, new BigDecimal("65.00"));

        when(groomingServiceRepository.existsByNameIgnoreCase("Full Groom")).thenReturn(false);
        when(groomingServiceRepository.save(any(GroomingService.class))).thenReturn(saved);

        GroomingServiceResponse response = groomingServiceService.create(request);

        ArgumentCaptor<GroomingService> captor = ArgumentCaptor.forClass(GroomingService.class);
        verify(groomingServiceRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Full Groom");
        assertThat(captor.getValue().getDescription()).isEqualTo("Includes bath and trim.");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsADuplicateName() {
        CreateGroomingServiceRequest request = new CreateGroomingServiceRequest(
                "Bath & Brush", null, 45, new BigDecimal("35.00"));
        when(groomingServiceRepository.existsByNameIgnoreCase("Bath & Brush")).thenReturn(true);

        assertThatThrownBy(() -> groomingServiceService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void returnsNotFoundForAnUnknownService() {
        when(groomingServiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groomingServiceService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }
}
