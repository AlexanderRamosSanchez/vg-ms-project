package pe.edu.vallegrande.database.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ✅ Evitar errores de stubbing innecesario
@DisplayName("AuthContextWebFilter Tests")
class AuthContextWebFilterTest {

    @Mock
    private WebFilterChain filterChain;

    private AuthContextWebFilter authContextWebFilter;

    @BeforeEach
    void setUp() {
        authContextWebFilter = new AuthContextWebFilter();
        // ✅ Solo configurar cuando sea necesario
    }

    @Test
    @DisplayName("Should create JWT token propagation filter")
    void shouldCreateJwtTokenPropagationFilter() {
        // When
        WebFilter filter = authContextWebFilter.jwtTokenPropagationFilter();

        // Then
        assertNotNull(filter);
    }

    @Test
    @DisplayName("Should propagate JWT token when Authorization header is present")
    void shouldPropagateJwtTokenWhenAuthorizationHeaderIsPresent() {
        // Given
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        WebFilter filter = authContextWebFilter.jwtTokenPropagationFilter();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token-123")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should continue without token when Authorization header is missing")
    void shouldContinueWithoutTokenWhenAuthorizationHeaderIsMissing() {
        // Given
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        WebFilter filter = authContextWebFilter.jwtTokenPropagationFilter();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should continue without token when Authorization header does not start with Bearer")
    void shouldContinueWithoutTokenWhenAuthorizationHeaderDoesNotStartWithBearer() {
        // Given
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        WebFilter filter = authContextWebFilter.jwtTokenPropagationFilter();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Basic dGVzdDp0ZXN0")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should handle empty Bearer token")
    void shouldHandleEmptyBearerToken() {
        // Given
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        WebFilter filter = authContextWebFilter.jwtTokenPropagationFilter();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
    }
}
