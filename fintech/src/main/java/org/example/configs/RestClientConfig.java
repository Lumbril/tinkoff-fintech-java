package org.example.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfig {
    @Value("${weatherapi.baseurl}")
    private String WEATHERAPI_BASE_URL;

    @Bean
    @Qualifier("weatherapi")
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(WEATHERAPI_BASE_URL)
                .build();
    }
}
