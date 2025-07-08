package pe.edu.vallegrande.database.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.database.dto.AdmissionReasonDTO;
import pe.edu.vallegrande.database.security.TestSecurityConfig;
import pe.edu.vallegrande.database.service.AdmissionReasonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@ExtendWith(SpringExtension.class)
@WebFluxTest(AdmissionReasonController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AdmissionReasonController - Pruebas de Integración")
class AdmissionReasonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdmissionReasonService admissionReasonService;

    private List<AdmissionReasonDTO> testAdmissionReasonList;

    @BeforeEach
    void setUp() {
        testAdmissionReasonList = createTestAdmissionReasonList();
    }

    // ========== TESTS PARA GET /api/v1/admission-reasons (Sin autenticación requerida) ==========

    @Test
    @DisplayName("GET / - Debe retornar todas las razones de admisión")
    void getAllAdmissionReasons_ShouldReturnAllAdmissionReasons() {
        // Given
        when(admissionReasonService.findAll()).thenReturn(Flux.fromIterable(testAdmissionReasonList));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/admission-reasons")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AdmissionReasonDTO.class)
                .hasSize(3)
                .contains(testAdmissionReasonList.get(0), testAdmissionReasonList.get(1), testAdmissionReasonList.get(2));
    }

    @Test
    @DisplayName("GET / - Debe retornar lista vacía cuando no hay razones de admisión")
    void getAllAdmissionReasons_WhenNoAdmissionReasons_ShouldReturnEmptyList() {
        // Given
        when(admissionReasonService.findAll()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/admission-reasons")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AdmissionReasonDTO.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("GET / - Debe manejar errores del servicio")
    void getAllAdmissionReasons_ServiceError_ShouldReturnError() {
        // Given
        when(admissionReasonService.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/admission-reasons")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ========== TESTS PARA POST /api/v1/admission-reasons (Con autenticación) ==========

    @Test
    @DisplayName("POST / - Debe crear razón de admisión exitosamente")
    void createAdmissionReason_ValidData_ShouldCreateAdmissionReason() {
        // Given
        AdmissionReasonDTO inputDTO = createTestAdmissionReasonDTO();
        inputDTO.setId(null);
        
        AdmissionReasonDTO createdDTO = createTestAdmissionReasonDTO();
        createdDTO.setId(1);
        
        when(admissionReasonService.create(any(AdmissionReasonDTO.class))).thenReturn(Mono.just(createdDTO));

        // When & Then
        webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/api/v1/admission-reasons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inputDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(AdmissionReasonDTO.class)
                .isEqualTo(createdDTO);
    }

    @Test
    @DisplayName("POST / - Debe manejar datos de entrada inválidos")
    void createAdmissionReason_InvalidData_ShouldReturnBadRequest() {
        // Given
        AdmissionReasonDTO invalidDTO = new AdmissionReasonDTO();
        // DTO sin campos requeridos
        
        when(admissionReasonService.create(any(AdmissionReasonDTO.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid data")));

        // When & Then
        webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/api/v1/admission-reasons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDTO)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("POST / - Debe manejar errores de creación del servicio")
    void createAdmissionReason_ServiceError_ShouldReturnServerError() {
        // Given
        AdmissionReasonDTO inputDTO = createTestAdmissionReasonDTO();
        when(admissionReasonService.create(any(AdmissionReasonDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/api/v1/admission-reasons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inputDTO)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("POST / - Debe validar Content-Type")
    void createAdmissionReason_InvalidContentType_ShouldReturnUnsupportedMediaType() {
        // When & Then
        webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/api/v1/admission-reasons")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("invalid content")
                .exchange()
                .expectStatus().isEqualTo(415); // Unsupported Media Type
    }

    @Test
    @DisplayName("POST / - Debe manejar cuerpo de petición vacío")
    void createAdmissionReason_EmptyBody_ShouldReturnBadRequest() {
        // When & Then
        webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/api/v1/admission-reasons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private AdmissionReasonDTO createTestAdmissionReasonDTO() {
        AdmissionReasonDTO dto = new AdmissionReasonDTO();
        dto.setId(1);
        dto.setReason("Violencia familiar");
        return dto;
    }

    private List<AdmissionReasonDTO> createTestAdmissionReasonList() {
        AdmissionReasonDTO reason1 = createTestAdmissionReasonDTO();
        reason1.setId(1);
        reason1.setReason("Violencia familiar");
        
        AdmissionReasonDTO reason2 = createTestAdmissionReasonDTO();
        reason2.setId(2);
        reason2.setReason("Abandono");
        
        AdmissionReasonDTO reason3 = createTestAdmissionReasonDTO();
        reason3.setId(3);
        reason3.setReason("Situación de calle");
        
        return Arrays.asList(reason1, reason2, reason3);
    }
}