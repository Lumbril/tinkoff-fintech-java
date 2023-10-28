package org.example;

import org.example.dto.response.WeatherTemperatureResponse;
import org.example.exceptions.WeatherAPIExceptions;
import org.example.services.WeatherAPIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WeatherAPIServiceTests {
    @Autowired
    private WeatherAPIService weatherAPIService;

    @Test
    public void happyPath() {
        WeatherTemperatureResponse response = weatherAPIService.get("Kazan");
        assertNotEquals(null, response.getTemperature());
    }

    @Test
    public void badCity() {
        WeatherAPIExceptions exception = assertThrows(WeatherAPIExceptions.class, () -> {
           WeatherTemperatureResponse response = weatherAPIService.get("NotExistsCity");
        });

        assertTrue(List.of(1006).contains(exception.getErrorCode()));
    }
}
