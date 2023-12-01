package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorResponseWeatherAPI;
import org.example.dto.response.WeatherTemperatureResponse;
import org.example.entities.City;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.exceptions.JsonException;
import org.example.exceptions.WeatherAPIExceptions;
import org.example.services.impl.CityServiceImpl;
import org.example.services.impl.WeatherServiceImpl;
import org.example.services.impl.WeatherTypeServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WeatherAPIService {
    @Value("${weatherapi.secret.key}")
    private String SECRET_KEY;

    private WebClient weatherWebClient;

    private RateLimiter rateLimiter;

    private WeatherServiceImpl weatherService;

    public WeatherAPIService(@Qualifier("weatherapi") WebClient webClient,
                             @Qualifier("ratelimiterWeatherapi") RateLimiter rateLimiter,
                             WeatherServiceImpl weatherService) {
        this.weatherWebClient = webClient;
        this.rateLimiter = rateLimiter;
        this.weatherService = weatherService;
    }

    public WeatherTemperatureResponse get(String city) {
        ResponseEntity<?> responseFromWeatherApi = weatherWebClient
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

        String jsonStr = (String) responseFromWeatherApi.getBody();
        ObjectMapper mapper = new ObjectMapper();
        WeatherTemperatureResponse response;

        try {
            JsonNode jsonNode = mapper.readTree(jsonStr);

            Weather w = weatherService.createFromJsonNode(jsonNode);

            response = WeatherTemperatureResponse.builder()
                    .temperature(w.getTemperature())
                    .build();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());

            throw new JsonException("Error in processing Json parsing");
        }

        return response;
    }
}
