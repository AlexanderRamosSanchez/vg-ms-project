package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.database.dto.AdmissionReasonDTO;
import pe.edu.vallegrande.database.model.AdmissionReason;
import pe.edu.vallegrande.database.repository.AdmissionReasonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdmissionReasonService Tests")
class AdmissionReasonServiceTest {

    @Mock
    private AdmissionReasonRepository admissionReasonRepository;

    @InjectMocks
    private AdmissionReasonService admissionReasonService;

    private AdmissionReason admissionReason1;
    private AdmissionReason admissionReason2;
    private AdmissionReasonDTO admissionReasonDTO;

    @BeforeEach
    @DisplayName("Setup test data")
    void setUp() {
        // Setup AdmissionReason entities
        admissionReason1 = new AdmissionReason();
        admissionReason1.setId(1);
        admissionReason1.setReason("Pobreza extrema");

        admissionReason2 = new AdmissionReason();
        admissionReason2.setId(2);
        admissionReason2.setReason("Desempleo");

        // Setup AdmissionReasonDTO
        admissionReasonDTO = new AdmissionReasonDTO();
        admissionReasonDTO.setId(3);
        admissionReasonDTO.setReason("Violencia familiar");
    }

    @Test
    @DisplayName("findAll should return all admission reasons as DTOs")
    void testFindAll_ShouldReturnAllAdmissionReasonsAsDTO() {
        // Given
        when(admissionReasonRepository.findAll())
                .thenReturn(Flux.just(admissionReason1, admissionReason2));

        // When & Then
        StepVerifier.create(admissionReasonService.findAll())
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getReason().equals("Pobreza extrema"))
                .expectNextMatches(dto -> 
                    dto.getId().equals(2) && 
                    dto.getReason().equals("Desempleo"))
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll should return empty flux when no admission reasons exist")
    void testFindAll_WithNoData_ShouldReturnEmptyFlux() {
        // Given
        when(admissionReasonRepository.findAll())
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(admissionReasonService.findAll())
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll should handle repository error")
    void testFindAll_WithRepositoryError_ShouldPropagateError() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        when(admissionReasonRepository.findAll())
                .thenReturn(Flux.error(repositoryError));

        // When & Then
        StepVerifier.create(admissionReasonService.findAll())
                .expectError(RuntimeException.class)
                .verify();

        verify(admissionReasonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById should return admission reason DTO when found")
    void testFindById_WithExistingId_ShouldReturnAdmissionReasonDTO() {
        // Given
        Integer id = 1;
        when(admissionReasonRepository.findById(id))
                .thenReturn(Mono.just(admissionReason1));

        // When & Then
        StepVerifier.create(admissionReasonService.findById(id))
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getReason().equals("Pobreza extrema"))
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById should handle repository error")
    void testFindById_WithRepositoryError_ShouldPropagateError() {
        // Given
        Integer id = 1;
        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        when(admissionReasonRepository.findById(id))
                .thenReturn(Mono.error(repositoryError));

        // When & Then
        StepVerifier.create(admissionReasonService.findById(id))
                .expectError(RuntimeException.class)
                .verify();

        verify(admissionReasonRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getReasonTextById should return reason text when found")
    void testGetReasonTextById_WithExistingId_ShouldReturnReasonText() {
        // Given
        Integer id = 1;
        when(admissionReasonRepository.findById(id))
                .thenReturn(Mono.just(admissionReason1));

        // When & Then
        StepVerifier.create(admissionReasonService.getReasonTextById(id))
                .expectNext("Pobreza extrema")
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getReasonTextById should return empty mono when admission reason not found")
    void testGetReasonTextById_WithNonExistingId_ShouldReturnEmptyMono() {
        // Given
        Integer id = 999;
        when(admissionReasonRepository.findById(id))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(admissionReasonService.getReasonTextById(id))
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("create should create new admission reason successfully")
    void testCreate_WithValidDTO_ShouldCreateAdmissionReason() {
        // Given
        AdmissionReasonDTO inputDTO = new AdmissionReasonDTO();
        inputDTO.setReason("Nueva razón de admisión");

        AdmissionReason savedAdmissionReason = new AdmissionReason();
        savedAdmissionReason.setId(10);
        savedAdmissionReason.setReason("Nueva razón de admisión");

        when(admissionReasonRepository.save(any(AdmissionReason.class)))
                .thenReturn(Mono.just(savedAdmissionReason));

        // When & Then
        StepVerifier.create(admissionReasonService.create(inputDTO))
                .expectNextMatches(dto -> 
                    dto.getId().equals(10) && 
                    dto.getReason().equals("Nueva razón de admisión"))
                .verifyComplete();

        verify(admissionReasonRepository, times(1)).save(any(AdmissionReason.class));
    }

    @Test
    @DisplayName("create should handle repository error during save")
    void testCreate_WithRepositoryError_ShouldPropagateError() {
        // Given
        AdmissionReasonDTO inputDTO = new AdmissionReasonDTO();
        inputDTO.setReason("Nueva razón");

        RuntimeException repositoryError = new RuntimeException("Database save failed");
        when(admissionReasonRepository.save(any(AdmissionReason.class)))
                .thenReturn(Mono.error(repositoryError));

        // When & Then
        StepVerifier.create(admissionReasonService.create(inputDTO))
                .expectError(RuntimeException.class)
                .verify();

        verify(admissionReasonRepository, times(1)).save(any(AdmissionReason.class));
    }

    @Test
    @DisplayName("create should verify that saved entity has correct data")
    void testCreate_ShouldVerifyEntityDataBeforeSave() {
        // Given
        AdmissionReasonDTO inputDTO = new AdmissionReasonDTO();
        inputDTO.setId(100); // Este ID debería ser ignorado
        inputDTO.setReason("Razón de prueba");

        AdmissionReason savedAdmissionReason = new AdmissionReason();
        savedAdmissionReason.setId(15);
        savedAdmissionReason.setReason("Razón de prueba");

        when(admissionReasonRepository.save(any(AdmissionReason.class)))
                .thenReturn(Mono.just(savedAdmissionReason));

        // When
        StepVerifier.create(admissionReasonService.create(inputDTO))
                .expectNextMatches(dto -> 
                    dto.getId().equals(15) && 
                    dto.getReason().equals("Razón de prueba"))
                .verifyComplete();

        // Then - verify the entity passed to save has correct data
        verify(admissionReasonRepository, times(1)).save(argThat(entity -> 
            entity.getId() == null && // ID should not be set in create
            entity.getReason().equals("Razón de prueba")
        ));
    }

    @Test
    @DisplayName("mapToDTO should handle admission reason with null values")
    void testMapToDTO_WithNullValues_ShouldHandleNullValues() {
        // Given
        AdmissionReason admissionReasonWithNulls = new AdmissionReason();
        admissionReasonWithNulls.setId(null);
        admissionReasonWithNulls.setReason(null);

        when(admissionReasonRepository.findById(anyInt()))
                .thenReturn(Mono.just(admissionReasonWithNulls));

        // When & Then
        StepVerifier.create(admissionReasonService.findById(1))
                .expectNextMatches(dto -> 
                    dto.getId() == null && 
                    dto.getReason() == null)
                .verifyComplete();
    }

    @Test
    @DisplayName("Service should handle multiple consecutive operations")
    void testMultipleOperations_ShouldWorkCorrectly() {
        // Given
        when(admissionReasonRepository.findAll())
                .thenReturn(Flux.just(admissionReason1, admissionReason2));
        when(admissionReasonRepository.findById(1))
                .thenReturn(Mono.just(admissionReason1));
        when(admissionReasonRepository.findById(2))
                .thenReturn(Mono.just(admissionReason2));

        // When & Then - Test findAll
        StepVerifier.create(admissionReasonService.findAll())
                .expectNextCount(2)
                .verifyComplete();

        // When & Then - Test findById
        StepVerifier.create(admissionReasonService.findById(1))
                .expectNextMatches(dto -> dto.getId().equals(1))
                .verifyComplete();

        // When & Then - Test getReasonTextById
        StepVerifier.create(admissionReasonService.getReasonTextById(2))
                .expectNext("Desempleo")
                .verifyComplete();

        // Verify all interactions
        verify(admissionReasonRepository, times(1)).findAll();
        verify(admissionReasonRepository, times(1)).findById(1);
        verify(admissionReasonRepository, times(1)).findById(2);
    }
}