package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorResponseWeatherAPI;
import org.example.dto.WeatherDto;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class WeatherAPIService {
    @Value("${weatherapi.secret.key}")
    private String SECRET_KEY;

    private WebClient weatherWebClient;

    private RateLimiter rateLimiter;

    private WeatherServiceImpl weatherService;

    private CacheService cacheService;

    public WeatherAPIService(@Qualifier("weatherapi") WebClient webClient,
                             @Qualifier("ratelimiterWeatherapi") RateLimiter rateLimiter,
                             WeatherServiceImpl weatherService,
                             CacheService cacheService) {
        this.weatherWebClient = webClient;
        this.rateLimiter = rateLimiter;
        this.weatherService = weatherService;
        this.cacheService = cacheService;
    }

    public WeatherTemperatureResponse get(String city) {
        Weather weather = cacheService.get(city);

        if (weather != null) {
            if (weather.getDateTime().until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.MINUTES) > 15) {
                weather = getFromWeatherApi(city);
                cacheService.update(city, weather);
            }
        } else {
            weather = weatherService.getByCity(city);

            if (weather == null || weather.getDateTime().until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.MINUTES) > 15) {
                weather = getFromWeatherApi(city);
            }

            cacheService.update(city, weather);
        }

        return WeatherTemperatureResponse.builder()
                .temperature(weather.getTemperature())
                .build();
    }

    private Weather getFromWeatherApi(String city) {
        ResponseEntity<?> responseFromWeatherApi = getRequestToWeatherAPI(city);

        String jsonStr = (String) responseFromWeatherApi.getBody();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(jsonStr);

            Weather w = weatherService.createFromJsonNode(jsonNode);

            return w;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());

            throw new JsonException("Error in processing Json parsing");
        }
    }

    public WeatherDto getWeatherDto(String city) {
        ResponseEntity<?> responseFromWeatherApi = getRequestToWeatherAPI(city);

        String jsonStr = (String) responseFromWeatherApi.getBody();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(jsonStr);

            WeatherDto w = WeatherDto.builder()
                    .city(jsonNode.get("location").get("name")
                            .toString()
                            .replaceAll("\"", ""))
                    .type(jsonNode
                            .get("current")
                            .get("condition")
                            .get("text")
                            .toString()
                            .replaceAll("\"", ""))
                    .temperature(Double.valueOf(String.valueOf(jsonNode.get("current").get("temp_c"))))
                    .dateTime(Instant.ofEpochSecond(
                            Long.valueOf(
                                    jsonNode
                                            .get("current")
                                            .get("last_updated_epoch")
                                            .toString()
                            )
                    ).atZone(
                            ZoneId.of("UTC")
                    ).toLocalDateTime())
                    .build();

            return w;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());

            throw new JsonException("Error in processing Json parsing");
        }
    }

    private ResponseEntity<?> getRequestToWeatherAPI(String city) {
        return weatherWebClient
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
    }
}
