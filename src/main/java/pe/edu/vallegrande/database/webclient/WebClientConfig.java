package pe.edu.vallegrande.database.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Value("${spring.family.service-url}")
    private String familyServiceUrl;

    @Bean
    public WebClient familyServiceWebClient() {
        return WebClient.builder()
                .baseUrl(familyServiceUrl)
                .filter(authHeaderFilter())
                .build();
    }

    private ExchangeFilterFunction authHeaderFilter() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            if (ctx.hasKey("Authorization")) {
                String token = ctx.get("Authorization");
                return next.exchange(
                        ClientRequest.from(request)
                                .headers(headers -> headers.setBearerAuth(token))
                                .build()
                );
            }
            return next.exchange(request);
        });
    }
}
