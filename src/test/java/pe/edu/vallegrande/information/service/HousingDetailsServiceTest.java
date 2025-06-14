package pe.edu.vallegrande.information.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.information.model.HousingDetails;
import pe.edu.vallegrande.information.repository.HousingDetailsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HousingDetailsServiceTest {

    @Mock
    private HousingDetailsRepository housingDetailsRepository;

    @InjectMocks
    private HousingDetailsService housingDetailsService;

    private HousingDetails housingDetails;
    private HousingDetails updatedHousingDetails;

    /**
     * Configura los objetos de prueba antes de cada método de prueba.
     * Inicializa un objeto HousingDetails con valores predeterminados y otro para actualizaciones.
     */
    @BeforeEach
    void setUp() {
        housingDetails = HousingDetails.builder()
                .id(1)
                .tenure("Propio")
                .typeOfHousing("Apartment")
                .housingMaterial("Concrete")
                .housingSecurity("High")
                .homeEnvironment(85)
                .bedroomNumber(3)
                .habitability("Good")
                .caregiverCondition("Available")
                .caringCondition("Good")
                .membersWork(2)
                .workingTime("Full-time")
                .monthlyIncome(BigDecimal.valueOf(1500.00))
                .monthlyExpense(BigDecimal.valueOf(800.00))
                .build();

        updatedHousingDetails = HousingDetails.builder()
                .id(1)
                .tenure("Propio")
                .typeOfHousing("Updated House")
                .housingMaterial("Updated Material")
                .housingSecurity("Updated Security")
                .homeEnvironment(90)
                .bedroomNumber(4)
                .habitability("Updated Habitability")
                .caregiverCondition("Updated Caregiver")
                .caringCondition("Updated Caring")
                .membersWork(3)
                .workingTime("Part-time")
                .monthlyIncome(BigDecimal.valueOf(2000.00))
                .monthlyExpense(BigDecimal.valueOf(900.00))
                .build();
    }

    /**
     * Verifica que se devuelvan todos los detalles de vivienda existentes.
     */
    @Test
    void findAll_ShouldReturnAllHousingDetails() {
        // Given
        when(housingDetailsRepository.findAll()).thenReturn(Flux.just(housingDetails));

        // When & Then
        StepVerifier.create(housingDetailsService.findAll())
                .expectNext(housingDetails)
                .verifyComplete();

        verify(housingDetailsRepository).findAll();
    }

    /**
     * Verifica que se devuelva un flujo vacío si no existen detalles de vivienda.
     */
    @Test
    void findAll_ShouldReturnEmptyFlux_WhenNoHousingDetailsExist() {
        // Given
        when(housingDetailsRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.findAll())
                .verifyComplete();

        verify(housingDetailsRepository).findAll();
    }

    /**
     * Verifica que se devuelvan los detalles de vivienda específicos cuando existen.
     */
    @Test
    void findById_ShouldReturnHousingDetails_WhenHousingDetailsExists() {
        // Given
        when(housingDetailsRepository.findById(1)).thenReturn(Mono.just(housingDetails));

        // When & Then
        StepVerifier.create(housingDetailsService.findById(1))
                .expectNext(housingDetails)
                .verifyComplete();

        verify(housingDetailsRepository).findById(1);
    }

    /**
     * Verifica que se devuelva vacío si los detalles de vivienda no existen.
     */
    @Test
    void findById_ShouldReturnEmpty_WhenHousingDetailsDoesNotExist() {
        // Given
        when(housingDetailsRepository.findById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.findById(999))
                .verifyComplete();

        verify(housingDetailsRepository).findById(999);
    }

    /**
     * Verifica que se devuelvan los detalles de vivienda guardados correctamente.
     */
    @Test
    void save_ShouldReturnSavedHousingDetails() {
        // Given
        when(housingDetailsRepository.save(any(HousingDetails.class))).thenReturn(Mono.just(housingDetails));

        // When & Then
        StepVerifier.create(housingDetailsService.save(housingDetails))
                .expectNext(housingDetails)
                .verifyComplete();

        verify(housingDetailsRepository).save(housingDetails);
    }

    /**
     * Verifica que se maneje correctamente el intento de guardar detalles de vivienda nulos.
     */
    @Test
    void save_ShouldHandleNullHousingDetails() {
        // Given
        when(housingDetailsRepository.save(any())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.save(null))
                .verifyComplete();

        verify(housingDetailsRepository).save(null);
    }

    /**
     * Verifica que se devuelvan los detalles de vivienda actualizados cuando existen.
     */
    @Test
    void update_ShouldReturnUpdatedHousingDetails_WhenHousingDetailsExists() {
        // Given
        when(housingDetailsRepository.findById(1)).thenReturn(Mono.just(housingDetails));
        when(housingDetailsRepository.save(any(HousingDetails.class))).thenReturn(Mono.just(updatedHousingDetails));

        // When & Then
        StepVerifier.create(housingDetailsService.update(1, updatedHousingDetails))
                .expectNext(updatedHousingDetails)
                .verifyComplete();

        verify(housingDetailsRepository).findById(1);
        verify(housingDetailsRepository).save(updatedHousingDetails);
    }

    /**
     * Verifica que se devuelva vacío si los detalles de vivienda no existen al intentar actualizar.
     */
    @Test
    void update_ShouldReturnEmpty_WhenHousingDetailsDoesNotExist() {
        // Given
        when(housingDetailsRepository.findById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.update(999, updatedHousingDetails))
                .verifyComplete();

        verify(housingDetailsRepository).findById(999);
        verify(housingDetailsRepository, never()).save(any());
    }

    /**
     * Verifica que se maneje correctamente el intento de actualizar detalles de vivienda con un DTO nulo.
     */
    @Test
    void update_ShouldHandleNullDTO() {
        // Given
        when(housingDetailsRepository.findById(1)).thenReturn(Mono.just(housingDetails));
        when(housingDetailsRepository.save(any(HousingDetails.class))).thenReturn(Mono.just(housingDetails));

        // When & Then
        StepVerifier.create(housingDetailsService.update(1, null))
                .expectNext(housingDetails)
                .verifyComplete();

        verify(housingDetailsRepository).findById(1);
        verify(housingDetailsRepository).save(housingDetails);
    }

    /**
     * Verifica que la eliminación de un detalle de vivienda se complete exitosamente.
     */
    @Test
    void delete_ShouldCompleteSuccessfully() {
        // Given
        when(housingDetailsRepository.deleteById(1)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.delete(1))
                .verifyComplete();

        verify(housingDetailsRepository).deleteById(1);
    }

    /**
     * Verifica que se maneje correctamente la eliminación de un ID no existente.
     */
    @Test
    void delete_ShouldHandleNonExistentId() {
        // Given
        when(housingDetailsRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(housingDetailsService.delete(999))
                .verifyComplete();

        verify(housingDetailsRepository).deleteById(999);
    }
}
