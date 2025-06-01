package pe.edu.vallegrande.database.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class FamilyServiceClient {

    private final WebClient webClient;

    public FamilyServiceClient(WebClient.Builder webClientBuilder,
                               @Value("${spring.family.service-url}") String familyServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(familyServiceUrl).build();
    }

    public Mono<Boolean> familyExists(Integer familyId) {
        return webClient.get()
                .uri("/api/v1/families/{id}", familyId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(error -> Mono.just(false));
    }
}
