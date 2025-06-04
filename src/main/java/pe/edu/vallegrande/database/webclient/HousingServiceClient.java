package pe.edu.vallegrande.database.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.edu.vallegrande.database.dto.BasicServiceDTO;
import pe.edu.vallegrande.database.dto.HousingDetailsDTO;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HousingServiceClient {

    private final WebClient housingServiceWebClient; // Inyectado desde WebClientConfig

    public Mono<BasicServiceDTO> getBasicServiceById(Integer serviceId) {
        if (serviceId == null) {
            return Mono.empty();
        }
        
        return housingServiceWebClient.get()
                .uri("/api/v1/services/{id}", serviceId)
                .retrieve()
                .bodyToMono(BasicServiceDTO.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<HousingDetailsDTO> getHousingDetailsById(Integer housingId) {
        if (housingId == null) {
            return Mono.empty();
        }
        
        return housingServiceWebClient.get()
                .uri("/api/v1/housing/{id}", housingId)
                .retrieve()
                .bodyToMono(HousingDetailsDTO.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<BasicServiceDTO> createBasicService(BasicServiceDTO basicServiceDTO) {
        return housingServiceWebClient.post()
                .uri("/api/v1/services")
                .bodyValue(basicServiceDTO)
                .retrieve()
                .bodyToMono(BasicServiceDTO.class);
    }

    public Mono<HousingDetailsDTO> createHousingDetails(HousingDetailsDTO housingDetailsDTO) {
        return housingServiceWebClient.post()
                .uri("/api/v1/housing")
                .bodyValue(housingDetailsDTO)
                .retrieve()
                .bodyToMono(HousingDetailsDTO.class);
    }

    public Mono<BasicServiceDTO> updateBasicService(Integer serviceId, BasicServiceDTO basicServiceDTO) {
        return housingServiceWebClient.put()
                .uri("/api/v1/services/{id}", serviceId)
                .bodyValue(basicServiceDTO)
                .retrieve()
                .bodyToMono(BasicServiceDTO.class);
    }

    public Mono<HousingDetailsDTO> updateHousingDetails(Integer housingId, HousingDetailsDTO housingDetailsDTO) {
        return housingServiceWebClient.put()
                .uri("/api/v1/housing/{id}", housingId)
                .bodyValue(housingDetailsDTO)
                .retrieve()
                .bodyToMono(HousingDetailsDTO.class);
    }
}