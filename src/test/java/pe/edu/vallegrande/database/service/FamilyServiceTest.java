package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.vallegrande.database.dto.AdmissionReasonDTO;
import pe.edu.vallegrande.database.dto.BasicServiceDTO;
import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.dto.HousingDetailsDTO;
import pe.edu.vallegrande.database.kafka.FamilyEventService;
import pe.edu.vallegrande.database.model.Family;
import pe.edu.vallegrande.database.repository.FamilyRepository;
import pe.edu.vallegrande.database.webclient.HousingServiceClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FamilyService - Pruebas Unitarias Completas")
class FamilyServiceTest {

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private FamilyEventService familyEventService;

    @Mock
    private FamilyMapper familyMapper;

    @Mock
    private HousingServiceClient housingServiceClient;

    @Mock
    private AdmissionReasonService admissionReasonService;

    @InjectMocks
    private FamilyService familyService;

    private Family testFamily;
    private FamilyDTO testFamilyDTO;
    private BasicServiceDTO testBasicService;
    private HousingDetailsDTO testHousingDetails;

    @BeforeEach
    void setUp() {
        testFamily = createTestFamily();
        testFamilyDTO = createTestFamilyDTO();
        testBasicService = createTestBasicService();
        testHousingDetails = createTestHousingDetails();
    }

    // ======================== PRUEBAS DE mapToFamilyDTO ========================

    @Test
    @DisplayName("Debe mapear Family a FamilyDTO con todos los servicios")
    void mapToFamilyDTO_WithAllServices_ShouldReturnCompleteFamilyDTO() {
        // Given
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.just("Razón de admisión"));
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getBasicService() != null && 
                    dto.getHousingDetails() != null &&
                    dto.getId().equals(testFamily.getId())
                )
                .verifyComplete();

        verify(admissionReasonService).getReasonTextById(testFamily.getReasibAdmission());
        verify(housingServiceClient).getBasicServiceById(testFamily.getServiceId());
        verify(housingServiceClient).getHousingDetailsById(testFamily.getHousingId());
    }

    @Test
    @DisplayName("Debe mapear Family a FamilyDTO sin servicios cuando no existen IDs")
    void mapToFamilyDTO_WithoutServices_ShouldReturnBasicFamilyDTO() {
        // Given
        testFamily.setServiceId(null);
        testFamily.setHousingId(null);
        testFamily.setReasibAdmission(null);
        
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getBasicService() == null && 
                    dto.getHousingDetails() == null
                )
                .verifyComplete();

        verify(admissionReasonService, never()).getReasonTextById(any());
        verify(housingServiceClient, never()).getBasicServiceById(any());
        verify(housingServiceClient, never()).getHousingDetailsById(any());
    }

    @Test
    @DisplayName("Debe mapear con solo serviceId presente")
    void mapToFamilyDTO_WithOnlyServiceId_ShouldMapPartially() {
        // Given
        testFamily.setHousingId(null);
        testFamily.setReasibAdmission(null);
        
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.just(testBasicService));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getBasicService() != null && 
                    dto.getHousingDetails() == null
                )
                .verifyComplete();

        verify(admissionReasonService, never()).getReasonTextById(any());
        verify(housingServiceClient).getBasicServiceById(testFamily.getServiceId());
        verify(housingServiceClient, never()).getHousingDetailsById(any());
    }

    @Test
    @DisplayName("Debe mapear con solo housingId presente")
    void mapToFamilyDTO_WithOnlyHousingId_ShouldMapPartially() {
        // Given
        testFamily.setServiceId(null);
        testFamily.setReasibAdmission(null);
        
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getBasicService() == null && 
                    dto.getHousingDetails() != null
                )
                .verifyComplete();

        verify(admissionReasonService, never()).getReasonTextById(any());
        verify(housingServiceClient, never()).getBasicServiceById(any());
        verify(housingServiceClient).getHousingDetailsById(testFamily.getHousingId());
    }

    @Test
    @DisplayName("Debe mapear con solo reasonAdmission presente")
    void mapToFamilyDTO_WithOnlyReasonAdmission_ShouldMapPartially() {
        // Given
        testFamily.setServiceId(null);
        testFamily.setHousingId(null);
        
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.just("Razón de admisión"));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getBasicService() == null && 
                    dto.getHousingDetails() == null
                )
                .verifyComplete();

        verify(admissionReasonService).getReasonTextById(testFamily.getReasibAdmission());
        verify(housingServiceClient, never()).getBasicServiceById(any());
        verify(housingServiceClient, never()).getHousingDetailsById(any());
    }

    @Test
    @DisplayName("Debe manejar errores en servicios externos y continuar")
    void mapToFamilyDTO_WithServiceErrors_ShouldHandleGracefully() {
        // Given
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error solo en basicService")
    void mapToFamilyDTO_WithBasicServiceError_ShouldHandleGracefully() {
        // Given
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.just("Razón de admisión"));
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getId().equals(testFamily.getId()) &&
                    dto.getHousingDetails() != null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error solo en housingDetails")
    void mapToFamilyDTO_WithHousingDetailsError_ShouldHandleGracefully() {
        // Given
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.just("Razón de admisión"));
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getId().equals(testFamily.getId()) &&
                    dto.getBasicService() != null
                )
                .verifyComplete();
    }

    // ======================== PRUEBAS DE CONSULTA ========================

    @Test
    @DisplayName("Debe obtener todas las familias activas ordenadas por ID")
    void findAllActive_ShouldReturnActiveFamiliesOrderedById() {
        // Given
        Family family1 = createTestFamily();
        family1.setId(2);
        Family family2 = createTestFamily();
        family2.setId(1);
        
        when(familyRepository.findAllByStatus("A")).thenReturn(Flux.just(family1, family2));
        when(familyMapper.toDTO(any(Family.class))).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.findAllActive())
                .expectNextCount(2)
                .verifyComplete();

        verify(familyRepository).findAllByStatus("A");
    }

    @Test
    @DisplayName("Debe retornar flujo vacío cuando no hay familias activas")
    void findAllActive_NoActiveFamilies_ShouldReturnEmptyFlux() {
        // Given
        when(familyRepository.findAllByStatus("A")).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(familyService.findAllActive())
                .verifyComplete();

        verify(familyRepository).findAllByStatus("A");
    }

    @Test
    @DisplayName("Debe obtener todas las familias inactivas")
    void findAllInactive_ShouldReturnInactiveFamilies() {
        // Given
        when(familyRepository.findAllByStatus("I")).thenReturn(Flux.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.findAllInactive())
                .expectNext(testFamilyDTO)
                .verifyComplete();

        verify(familyRepository).findAllByStatus("I");
    }

    @Test
    @DisplayName("Debe retornar flujo vacío cuando no hay familias inactivas")
    void findAllInactive_NoInactiveFamilies_ShouldReturnEmptyFlux() {
        // Given
        when(familyRepository.findAllByStatus("I")).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(familyService.findAllInactive())
                .verifyComplete();

        verify(familyRepository).findAllByStatus("I");
    }

    @Test
    @DisplayName("Debe encontrar familia por ID exitosamente")
    void findById_ExistingFamily_ShouldReturnFamilyDTO() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.findById(1))
                .expectNext(testFamilyDTO)
                .verifyComplete();

        verify(familyRepository).findById(1);
    }

    @Test
    @DisplayName("Debe completar vacío cuando la familia no existe")
    void findById_NonExistingFamily_ShouldCompleteEmpty() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.findById(999))
                .verifyComplete();

        verify(familyRepository).findById(999);
    }

    // ======================== PRUEBAS DE CREACIÓN ========================

    @Test
    @DisplayName("Debe crear familia exitosamente con todos los servicios")
    void createFamily_WithAllServices_ShouldCreateSuccessfully() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(testBasicService);
        testFamilyDTO.setHousingDetails(testHousingDetails);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(housingServiceClient.createBasicService(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.createHousingDetails(any())).thenReturn(Mono.just(testHousingDetails));
        when(familyMapper.toEntity(testFamilyDTO)).thenReturn(testFamily);
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.createFamily(testFamilyDTO))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();

        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("CREATED"));
    }

    @Test
    @DisplayName("Debe fallar al crear familia con razón de admisión inválida")
    void createFamily_WithInvalidAdmissionReason_ShouldFail() {
        // Given
        testFamilyDTO.setReasibAdmission(999);
        when(admissionReasonService.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.createFamily(testFamilyDTO))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error al crear basicService")
    void createFamily_WithBasicServiceError_ShouldFail() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(testBasicService);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(housingServiceClient.createBasicService(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(familyService.createFamily(testFamilyDTO))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ======================== PRUEBAS DE ACTUALIZACIÓN ========================

    @Test
    @DisplayName("Debe actualizar familia exitosamente con todos los servicios")
    void updateFamily_WithAllServices_ShouldUpdateSuccessfully() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(testBasicService);
        testFamilyDTO.setHousingDetails(testHousingDetails);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(housingServiceClient.updateBasicService(any(), any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.updateHousingDetails(any(), any())).thenReturn(Mono.just(testHousingDetails));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();

        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    @DisplayName("Debe actualizar familia sin servicios adicionales")
    void updateFamily_WithoutServices_ShouldUpdateSuccessfully() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(null);
        testFamilyDTO.setHousingDetails(null);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();

        verify(housingServiceClient, never()).updateBasicService(any(), any());
        verify(housingServiceClient, never()).updateHousingDetails(any(), any());
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    @DisplayName("Debe fallar al actualizar familia inexistente")
    void updateFamily_NonExistingFamily_ShouldFail() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.updateFamily(999, testFamilyDTO))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error al actualizar basicService")
    void updateFamily_WithBasicServiceError_ShouldFail() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(testBasicService);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(housingServiceClient.updateBasicService(any(), any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error al actualizar housingDetails")
    void updateFamily_WithHousingDetailsError_ShouldFail() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setHousingDetails(testHousingDetails);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(housingServiceClient.updateHousingDetails(any(), any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe actualizar solo con basicService")
    void updateFamily_WithOnlyBasicService_ShouldUpdateSuccessfully() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(testBasicService);
        testFamilyDTO.setHousingDetails(null);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(housingServiceClient.updateBasicService(any(), any())).thenReturn(Mono.just(testBasicService));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();

        verify(housingServiceClient).updateBasicService(any(), any());
        verify(housingServiceClient, never()).updateHousingDetails(any(), any());
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    @DisplayName("Debe actualizar solo con housingDetails")
    void updateFamily_WithOnlyHousingDetails_ShouldUpdateSuccessfully() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        testFamilyDTO.setBasicService(null);
        testFamilyDTO.setHousingDetails(testHousingDetails);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(housingServiceClient.updateHousingDetails(any(), any())).thenReturn(Mono.just(testHousingDetails));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectNextMatches(dto -> dto.getId().equals(testFamily.getId()))
                .verifyComplete();

        verify(housingServiceClient, never()).updateBasicService(any(), any());
        verify(housingServiceClient).updateHousingDetails(any(), any());
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    // ======================== PRUEBAS DE ELIMINACIÓN Y ACTIVACIÓN ========================

    @Test
    @DisplayName("Debe eliminar familia lógicamente")
    void deleteFamily_ExistingFamily_ShouldSetStatusToInactive() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));

        // When & Then
        StepVerifier.create(familyService.deleteFamily(1))
                .verifyComplete();

        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("DELETED"));
    }

    @Test
    @DisplayName("Debe activar familia exitosamente")
    void activeFamily_ExistingFamily_ShouldSetStatusToActive() {
        // Given
        testFamily.setStatus("I");
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));

        // When & Then
        StepVerifier.create(familyService.activeFamily(1))
                .verifyComplete();

        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar familia inexistente")
    void deleteFamily_NonExistingFamily_ShouldThrowException() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.deleteFamily(999))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar excepción al activar familia inexistente")
    void activeFamily_NonExistingFamily_ShouldThrowException() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.activeFamily(999))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error al guardar durante eliminación")
    void deleteFamily_SaveError_ShouldPropagateError() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.error(new RuntimeException("Save error")));

        // When & Then
        StepVerifier.create(familyService.deleteFamily(1))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error al guardar durante activación")
    void activeFamily_SaveError_ShouldPropagateError() {
        // Given
        testFamily.setStatus("I");
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.error(new RuntimeException("Save error")));

        // When & Then
        StepVerifier.create(familyService.activeFamily(1))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe obtener detalles de familia existente")
    void findDetailById_ExistingFamily_ShouldReturnFamilyDetails() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(any())).thenReturn(Mono.just("Razón"));
        when(housingServiceClient.getBasicServiceById(any())).thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(any())).thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.findDetailById(1))
                .expectNext(testFamilyDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe completar vacío al buscar detalles de familia inexistente")
    void findDetailById_NonExistingFamily_ShouldCompleteEmpty() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.findDetailById(999))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error en repositorio durante búsqueda de detalles")
    void findDetailById_RepositoryError_ShouldPropagateError() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.error(new RuntimeException("Repository error")));

        // When & Then
        StepVerifier.create(familyService.findDetailById(1))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ======================== PRUEBAS ADICIONALES PARA COBERTURA ========================

    @Test
    @DisplayName("Debe manejar error en repositorio durante findAllActive")
    void findAllActive_RepositoryError_ShouldPropagateError() {
        // Given
        when(familyRepository.findAllByStatus("A")).thenReturn(Flux.error(new RuntimeException("Repository error")));

        // When & Then
        StepVerifier.create(familyService.findAllActive())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error en repositorio durante findAllInactive")
    void findAllInactive_RepositoryError_ShouldPropagateError() {
        // Given
        when(familyRepository.findAllByStatus("I")).thenReturn(Flux.error(new RuntimeException("Repository error")));

        // When & Then
        StepVerifier.create(familyService.findAllInactive())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error en repositorio durante findById")
    void findById_RepositoryError_ShouldPropagateError() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.error(new RuntimeException("Repository error")));

        // When & Then
        StepVerifier.create(familyService.findById(1))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar error en repositorio durante save en updateFamily")
    void updateFamily_RepositorySaveError_ShouldPropagateError() {
        // Given
        testFamilyDTO.setReasibAdmission(1);
        
        when(admissionReasonService.findById(1)).thenReturn(Mono.just(createAdmissionReason()));
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.error(new RuntimeException("Repository error")));

        // When & Then
        StepVerifier.create(familyService.updateFamily(1, testFamilyDTO))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar solo error en reasonService")
    void mapToFamilyDTO_WithOnlyReasonServiceError_ShouldHandleGracefully() {
        // Given
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(admissionReasonService.getReasonTextById(testFamily.getReasibAdmission()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));
        when(housingServiceClient.getBasicServiceById(testFamily.getServiceId()))
                .thenReturn(Mono.just(testBasicService));
        when(housingServiceClient.getHousingDetailsById(testFamily.getHousingId()))
                .thenReturn(Mono.just(testHousingDetails));

        // When & Then
        StepVerifier.create(familyService.mapToFamilyDTO(testFamily))
                .expectNextMatches(dto -> 
                    dto.getId().equals(testFamily.getId()) &&
                    dto.getBasicService() != null &&
                    dto.getHousingDetails() != null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe validar que el status se actualice correctamente en deleteFamily")
    void deleteFamily_ShouldUpdateStatusToInactive() {
        // Given
        testFamily.setStatus("A");
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(argThat(family -> "I".equals(family.getStatus()))))
                .thenReturn(Mono.just(testFamily));

        // When & Then
        StepVerifier.create(familyService.deleteFamily(1))
                .verifyComplete();

        verify(familyRepository).save(argThat(family -> "I".equals(family.getStatus())));
    }

    @Test
    @DisplayName("Debe validar que el status se actualice correctamente en activeFamily")
    void activeFamily_ShouldUpdateStatusToActive() {
        // Given
        testFamily.setStatus("I");
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(argThat(family -> "A".equals(family.getStatus()))))
                .thenReturn(Mono.just(testFamily));

        // When & Then
        StepVerifier.create(familyService.activeFamily(1))
                .verifyComplete();

        verify(familyRepository).save(argThat(family -> "A".equals(family.getStatus())));
    }

    // ======================== MÉTODOS AUXILIARES ========================

    private Family createTestFamily() {
        Family family = new Family();
        family.setId(1);
        family.setLastName("Pérez");
        family.setDirection("Av. Principal 123");
        family.setNumberMembers(4);
        family.setNumberChildren(2);
        family.setStatus("A");
        family.setServiceId(100);
        family.setHousingId(200);
        family.setReasibAdmission(1);
        family.setCreated(LocalDateTime.now());
        return family;
    }

    private FamilyDTO createTestFamilyDTO() {
        FamilyDTO dto = new FamilyDTO();
        dto.setId(1);
        dto.setLastName("Pérez");
        dto.setDirection("Av. Principal 123");
        dto.setNumberMembers(4);
        dto.setNumberChildren(2);
        dto.setStatus("A");
        dto.setReasibAdmission(1);
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    private BasicServiceDTO createTestBasicService() {
        return BasicServiceDTO.builder()
                .serviceId(100)
                .waterService("Si")
                .servLight("Si")
                .area("Urbana")
                .build();
    }

    private HousingDetailsDTO createTestHousingDetails() {
        return HousingDetailsDTO.builder()
                .id(200)
                .typeOfHousing("Casa")
                .housingMaterial("Material Noble")
                .bedroomNumber(3)
                .build();
    }

    private AdmissionReasonDTO createAdmissionReason() {
        AdmissionReasonDTO dto = new AdmissionReasonDTO();
        dto.setId(1);
        dto.setReason("Razón de admisión de prueba");
        return dto;
    }
}