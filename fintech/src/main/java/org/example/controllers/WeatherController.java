package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Weather;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.WeatherTemperatureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Weather", description = "Weather API")
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    public static List<Weather> weatherList = new ArrayList<>();

    public WeatherController() {
        WeatherController.weatherList.add(new Weather(
                1L,
                "Казань",
                20.,
                LocalDateTime.now()
        ));
    }

    @Operation(summary = "Получить по городу температуру на текущую дату")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WeatherTemperatureResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @GetMapping(value = {"/{city}"})
    public ResponseEntity<?> doGet(@PathVariable String city) {
        return ResponseEntity.ok().body(
                new WeatherTemperatureResponse(
                        weatherList.stream()
                        .filter(weather -> weather.getRegionName().equals(city))
                        .filter(weather -> weather.getDate().toLocalDate().compareTo(LocalDate.now()) == 0)
                        .findFirst().orElseThrow()
                )
        );
    }

    @PostMapping(value = "/{city}")
    public ResponseEntity<?> doPost(@PathVariable String city) {

    }

    @PutMapping(value = "/{city}")
    public ResponseEntity<?> doPut(@PathVariable String city) {

    }

    @Operation(summary = "Удалить город")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @DeleteMapping(value = "/{city}")
    public ResponseEntity<?> doDelete(@PathVariable String city) {
        List<Weather> weathersForRemove = weatherList.stream()
                .filter(weather -> weather.getRegionName().equals(city))
                .toList();

        if (weathersForRemove.isEmpty()) {
            throw new NoSuchElementException("No value present");
        }

        weatherList = weatherList.stream()
                .filter(weather -> !weathersForRemove.contains(weather))
                .toList();

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .error(e.getMessage())
                        .build()
        );
    }
}
