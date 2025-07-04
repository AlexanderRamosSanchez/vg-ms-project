package pe.edu.vallegrande.database.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configuración de seguridad para el microservicio Family.
 * - Permite acceso libre a Swagger y documentación
 * - GET: requiere rol USER o ADMIN
 * - POST/PUT/DELETE: requiere rol ADMIN
 * - Configura CORS para permitir acceso desde frontend
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        // Swagger y documentación (acceso libre)
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()

                        // API endpoints con autenticación
                        // GET: accesible por USER y ADMIN
                        .pathMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER")

                        // POST, PUT, DELETE: solo ADMIN
                        .pathMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // Lo demás requiere autenticación
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .cors(cors -> cors
                        .configurationSource(exchange -> {
                            var config = new CorsConfiguration();
                            config.setAllowCredentials(true);
                            config.setAllowedOriginPatterns(List.of("*"));
                            config.addAllowedHeader("*");
                            config.addAllowedMethod("*");
                            return config;
                        })
                )
                .build();
    }
}
