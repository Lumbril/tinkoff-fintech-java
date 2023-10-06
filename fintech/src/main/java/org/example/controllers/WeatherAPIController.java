package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.response.ErrorResponse;
import org.example.exceptions.WeatherAPIExceptions;
import org.example.services.WeatherAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "WeaterAPI", description = "API for remote service WeatherAPI")
@RestController
@RequestMapping("/v1")
public class WeatherAPIController {
    private final ArrayList<Integer> errorsFor500 = new ArrayList<>(
            List.of(1002, 1003, 2006, 2008)
    );
    private final ArrayList<Integer> errorsFor503 = new ArrayList<>(
            List.of(1005, 2007, 2009)
    );
    private final ArrayList<Integer> errorsFor400 = new ArrayList<>(
            List.of(1006)
    );

    @Autowired
    private WeatherAPIService weatherAPIService;

    @Operation(summary = "Получить погоду от сервиса WeatherAPI")
    @GetMapping("/current.json")
    public ResponseEntity<?> doGet(@RequestParam String city) {
        return weatherAPIService.get(city);
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

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .error("Неизвестная ошибка")
                                .build()
                );
    }
}
