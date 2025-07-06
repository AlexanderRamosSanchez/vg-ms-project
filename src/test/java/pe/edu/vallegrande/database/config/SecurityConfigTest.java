package pe.edu.vallegrande.database.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Mock
    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    @DisplayName("Should create SecurityConfig with JWT converter")
    void shouldCreateSecurityConfigWithJwtConverter() {
        // Given & When
        SecurityConfig config = new SecurityConfig(jwtAuthenticationConverter);

        // Then
        assertNotNull(config);
    }

    @Test
    @DisplayName("Should handle null JWT converter gracefully")
    void shouldHandleNullJwtConverterGracefully() {
        // Given & When
        SecurityConfig config = new SecurityConfig(null);

        // Then
        assertNotNull(config);
    }
}
