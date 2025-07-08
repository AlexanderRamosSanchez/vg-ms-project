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
    @DisplayName("Debe crear familia exitosamente con servicios")
    void createFamily_WithServices_ShouldCreateSuccessfully() {
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

    // ======================== PRUEBAS DE ACTUALIZACIÓN ========================

    @Test
    @DisplayName("Debe actualizar familia exitosamente")
    void updateFamily_ExistingFamily_ShouldUpdateSuccessfully() {
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
    @DisplayName("Debe lanzar excepción al cambiar status de familia inexistente")
    void changeStatus_NonExistingFamily_ShouldThrowException() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(familyService.deleteFamily(999))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe obtener detalles de familia")
    void findDetailById_ShouldReturnFamilyDetails() {
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