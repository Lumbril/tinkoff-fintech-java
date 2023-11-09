package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Weather;
import org.example.dto.request.WeatherRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.WeatherResponse;
import org.example.dto.response.WeatherTemperatureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Weather", description = "Weather API")
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    public static Long lastId = 1L;
    public static List<Weather> weatherList = new ArrayList<>();

    public WeatherController() {
        WeatherController.weatherList.add(new Weather(
                lastId++,
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
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
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

    @Operation(summary = "Создать новый город")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WeatherResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/{city}")
    public ResponseEntity<?> doPost(@PathVariable String city,
                                    @Validated @RequestBody WeatherRequest weatherRequest) {
        Weather weather = new Weather(lastId++, city, weatherRequest.getTemperature(), weatherRequest.getDate());
        weatherList.add(weather);

        return ResponseEntity.ok().body(
                new WeatherResponse(weather)
        );
    }

    @Operation(summary = "Обновить температуру в городе или создать новую запись")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WeatherResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{city}")
    public ResponseEntity<?> doPut(@PathVariable String city,
                                   @Validated @RequestBody WeatherRequest weatherRequest) {
        Weather weather = weatherList.stream()
                .filter(w -> w.getRegionName().equals(city))
                .filter(w -> w.getDate().toLocalDate().equals(weatherRequest.getDate().toLocalDate()))
                .findFirst().orElse(null);

        if (weather == null) {
            weather = new Weather(lastId++, city, weatherRequest.getTemperature(), weatherRequest.getDate());
            weatherList.add(weather);
        } else {
            weather.setTemperature(weatherRequest.getTemperature());
        }

        return ResponseEntity.ok().body(
                new WeatherResponse(weather)
        );
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestValue(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Ошибка валидации")
                        .build()
        );
    }
}
