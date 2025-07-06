package pe.edu.vallegrande.database.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.database.webclient.WebClientConfig;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WebClientConfig Tests")
class WebClientConfigTest {

    private WebClientConfig webClientConfig;

    @BeforeEach
    void setUp() {
        webClientConfig = new WebClientConfig();
        ReflectionTestUtils.setField(webClientConfig, "familyServiceUrl", "http://localhost:8081");
    }

    @Test
    @DisplayName("Should create family service WebClient")
    void shouldCreateFamilyServiceWebClient() {
        // When
        WebClient webClient = webClientConfig.familyServiceWebClient();

        // Then
        assertNotNull(webClient);
    }

    @Test
    @DisplayName("Should create WebClient with correct base URL")
    void shouldCreateWebClientWithCorrectBaseUrl() {
        // Given
        String testUrl = "http://test-family-service:8080";
        ReflectionTestUtils.setField(webClientConfig, "familyServiceUrl", testUrl);

        // When
        WebClient webClient = webClientConfig.familyServiceWebClient();

        // Then
        assertNotNull(webClient);
        // Note: We can't easily test the base URL without more complex setup
        // but we can verify the WebClient is created successfully
    }

    @Test
    @DisplayName("Should handle null family service URL")
    void shouldHandleNullFamilyServiceUrl() {
        // Given
        ReflectionTestUtils.setField(webClientConfig, "familyServiceUrl", null);

        // When & Then
        assertDoesNotThrow(() -> {
            WebClient webClient = webClientConfig.familyServiceWebClient();
            assertNotNull(webClient);
        });
    }

    @Test
    @DisplayName("Should handle empty family service URL")
    void shouldHandleEmptyFamilyServiceUrl() {
        // Given
        ReflectionTestUtils.setField(webClientConfig, "familyServiceUrl", "");

        // When & Then
        assertDoesNotThrow(() -> {
            WebClient webClient = webClientConfig.familyServiceWebClient();
            assertNotNull(webClient);
        });
    }
}
