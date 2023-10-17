package org.example.services;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.example.dto.ErrorResponseWeatherAPI;
import org.example.exceptions.WeatherAPIExceptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherAPIService {
    @Value("${weatherapi.secret.key}")
    private String SECRET_KEY;

    private WebClient weatherWebClient;

    private RateLimiter rateLimiter;

    public WeatherAPIService(@Qualifier("weatherapi") WebClient webClient,
                             @Qualifier("ratelimiterWeatherapi") RateLimiter rateLimiter) {
        this.weatherWebClient = webClient;
        this.rateLimiter = rateLimiter;
    }

    public ResponseEntity<?> get(String city) {
        ResponseEntity<?> response = weatherWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("key", SECRET_KEY)
                        .queryParam("q", city)
                        .build()
                ).retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.is4xxClientError() ||
                                httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(ErrorResponseWeatherAPI.class)
                                .flatMap(errorResponseWeatherAPI -> Mono.error(
                                            new WeatherAPIExceptions(
                                                    errorResponseWeatherAPI.getError().getMessage(),
                                                    errorResponseWeatherAPI.getError().getCode()
                                            )
                                        )
                                )
                )
                .toEntity(String.class)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .block();

        return response;
    }
}
