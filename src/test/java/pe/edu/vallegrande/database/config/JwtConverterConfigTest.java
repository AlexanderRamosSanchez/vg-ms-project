package pe.edu.vallegrande.database.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtConverterConfig Tests")
class JwtConverterConfigTest {

    private JwtConverterConfig jwtConverterConfig;

    @BeforeEach
    void setUp() {
        jwtConverterConfig = new JwtConverterConfig();
    }

    @Test
    @DisplayName("Debería crear un convertidor de autenticación JWT")
    void shouldCreateJwtAuthenticationConverter() {
        // When
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();

        // Then
        assertNotNull(converter);
    }

    @Test
    @DisplayName("Debe convertir JWT con rol ADMIN en token de autenticación")
    void shouldConvertJwtWithAdminRoleToAuthenticationToken() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("sub", "user123"); // ✅ Agregar subject requerido
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().stream()
                                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe convertir JWT con rol de USUARIO en token de autenticación")
    void shouldConvertJwtWithUserRoleToAuthenticationToken() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("sub", "user456"); // ✅ Agregar subject requerido
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().stream()
                                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority()));
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Se debe crear un token sin autoridad cuando JWT no tiene reclamo de rol")
    void shouldCreateTokenWithNoAuthoritiesWhenJwtHasNoRoleClaim() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user789"); // ✅ Agregar subject requerido, sin role
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().isEmpty();
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Se debe crear un token sin autoridad cuando JWT tiene un reclamo de rol vacío")
    void shouldCreateTokenWithNoAuthoritiesWhenJwtHasEmptyRoleClaim() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "");
        claims.put("sub", "user101"); // ✅ Agregar subject requerido
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().isEmpty();
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Se debe crear un token sin autoridad cuando JWT tiene un reclamo de rol de solo espacios en blanco")
    void shouldCreateTokenWithNoAuthoritiesWhenJwtHasWhitespaceOnlyRoleClaim() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "   ");
        claims.put("sub", "user202"); // ✅ Agregar subject requerido
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().isEmpty();
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería gestionar JWT con múltiples reclamaciones correctamente")
    void shouldHandleJwtWithMultipleClaimsCorrectly() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("sub", "user123");
        claims.put("iss", "test-issuer");
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().stream()
                                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority())) &&
                               jwtToken.getToken().getSubject().equals("user123");
                    }
                    return false;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar caracteres especiales en la reclamación de roles")
    void shouldHandleSpecialCharactersInRoleClaim() {
        // Given
        ReactiveJwtAuthenticationConverter converter = jwtConverterConfig.jwtAuthenticationConverter();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "super-admin");
        claims.put("sub", "admin123"); // ✅ Agregar subject requerido
        
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "HS256"), claims);

        // When
        StepVerifier.create(converter.convert(jwt))
                .expectNextMatches(authToken -> {
                    if (authToken instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authToken;
                        return jwtToken.getAuthorities().stream()
                                .anyMatch(auth -> "ROLE_SUPER-ADMIN".equals(auth.getAuthority()));
                    }
                    return false;
                })
                .verifyComplete();
    }
}
