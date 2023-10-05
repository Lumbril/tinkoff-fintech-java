package org.example.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfig {
    private String WEATHERAPI_BASE_URL = "https://api.weatherapi.com/v1/current.json";

    @Bean
    @Qualifier("weatherapi")
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(WEATHERAPI_BASE_URL)
                .build();
    }
}
