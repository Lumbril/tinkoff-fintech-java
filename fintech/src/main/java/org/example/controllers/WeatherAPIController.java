package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.WeatherTemperatureResponse;
import org.example.exceptions.JsonException;
import org.example.exceptions.WeatherAPIExceptions;
import org.example.services.WeatherAPIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WeaterAPI", description = "API for remote service WeatherAPI")
@RestController
@RequestMapping("/v1")
public class WeatherAPIController {
    private final List<Integer> errorsFor500 = List.of(1002, 1003, 2006, 2008);
    private final List<Integer> errorsFor503 = List.of(1005, 2007, 2009);
    private final List<Integer> errorsFor400 = List.of(1006);

    private WeatherAPIService weatherAPIService;

    public WeatherAPIController(WeatherAPIService weatherAPIService) {
        this.weatherAPIService = weatherAPIService;
    }

    @Operation(summary = "Получить погоду от сервиса WeatherAPI")
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
                    responseCode = "400",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "503",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @GetMapping("/current.json")
    public ResponseEntity<?> doGet(@RequestParam String city) {
        WeatherTemperatureResponse response = weatherAPIService.get(city);

        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(WeatherAPIExceptions.class)
    public ResponseEntity<?> handle(WeatherAPIExceptions exceptions) {
        if (errorsFor500.contains(exceptions.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ErrorResponse.builder()
                                    .error("Внутренняя ошибка сервера")
                                    .build()
                    );
        }

        if (errorsFor503.contains(exceptions.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(
                            ErrorResponse.builder()
                                    .error("Сервис не доступен")
                                    .build()
                    );
        }

        if (errorsFor400.contains(exceptions.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            ErrorResponse.builder()
                                    .error(exceptions.getMessage())
                                    .build()
                    );
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .error("Неизвестная ошибка")
                                .build()
                );
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<?> handle(RequestNotPermitted permitted) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        ErrorResponse.builder()
                                .error("Превышено число запросов")
                                .build()
                );
    }

    @ExceptionHandler(JsonException.class)
    public ResponseEntity<?> handle(JsonException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .error("Внутренняя ошибка сервера: " + exception.getMessage())
                                .build()
                );
    }
}
