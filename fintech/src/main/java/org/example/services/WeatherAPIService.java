package org.example.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherAPIService {
    @Value("${weatherapi.secret.key}")
    private String SECRET_KEY;

    private WebClient weatherWebClient;

    public WeatherAPIService(@Qualifier("weatherapi") WebClient webClient) {
        this.weatherWebClient = webClient;
    }

    public ResponseEntity<String> get(String city) {
        ResponseEntity<String> response = weatherWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("key", SECRET_KEY)
                        .queryParam("q", city)
                        .build()
                ).retrieve()
                .toEntity(String.class)
                .block();

        return response;
    }
}
