package pe.edu.vallegrande.database.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Configuration Tests")
class ConfigurationTest {

    @Test
    @DisplayName("Should test configuration classes instantiation")
    void shouldTestConfigurationClassesInstantiation() {
        // Given & When
        SecurityConfig securityConfig = new SecurityConfig(null);
        JwtConverterConfig jwtConverterConfig = new JwtConverterConfig();
        AuthContextWebFilter authContextWebFilter = new AuthContextWebFilter();
        
        // Then
        assertNotNull(securityConfig);
        assertNotNull(jwtConverterConfig);
        assertNotNull(authContextWebFilter);
    }

    @Test
    @DisplayName("Should test JWT converter creation")
    void shouldTestJwtConverterCreation() {
        // Given
        JwtConverterConfig config = new JwtConverterConfig();
        
        // When & Then
        assertDoesNotThrow(() -> {
            var converter = config.jwtAuthenticationConverter();
            assertNotNull(converter);
        });
    }

    @Test
    @DisplayName("Should test web filter creation")
    void shouldTestWebFilterCreation() {
        // Given
        AuthContextWebFilter config = new AuthContextWebFilter();
        
        // When & Then
        assertDoesNotThrow(() -> {
            var filter = config.jwtTokenPropagationFilter();
            assertNotNull(filter);
        });
    }
}
