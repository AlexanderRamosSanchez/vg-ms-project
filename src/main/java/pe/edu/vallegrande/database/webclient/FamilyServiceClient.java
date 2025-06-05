package pe.edu.vallegrande.database.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FamilyServiceClient {

    private final WebClient familyServiceWebClient; // Inyectado desde WebClientConfig

    public Mono<Boolean> familyExists(Integer familyId) {
        if (familyId == null) {
            return Mono.just(false);
        }
        
        return familyServiceWebClient.get()
                .uri("/api/v1/families/{id}", familyId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(error -> Mono.just(false));
    }
}
