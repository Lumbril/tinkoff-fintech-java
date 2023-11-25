package org.example;

import org.example.dto.response.WeatherTemperatureResponse;
import org.example.entities.City;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.services.CacheService;
import org.example.services.WeatherAPIService;
import org.example.services.impl.WeatherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CacheTests {
    @Mock
    private CacheService cacheService;

    @Mock
    private WeatherServiceImpl weatherService;

    @InjectMocks
    private WeatherAPIService weatherAPIService;

    @Test
    public void addedWeatherInCacheTest() {
        City c = new City(1L, "Казань");
        WeatherType wt = new WeatherType(1L, "Солнечно");

        Weather w = new Weather(1L, 23., LocalDateTime.now(ZoneOffset.UTC), wt, c);

        when(weatherService.getByCity("Казань")).thenReturn(w);
        when(cacheService.update("Казань", w)).thenReturn(w);

        weatherAPIService.get("Казань");

        verify(cacheService, times(1)).update("Казань", w);
    }

    @Test
    public void getDataFromCacheWithoutDBTest() {
        City c = new City(1L, "Казань");
        WeatherType wt = new WeatherType(1L, "Солнечно");

        Weather w = new Weather(1L, 23., LocalDateTime.now(ZoneOffset.UTC), wt, c);

        when(cacheService.get("Казань")).thenReturn(w);

        WeatherTemperatureResponse weatherTemperatureResponse = weatherAPIService.get("Казань");

        verify(weatherService, never()).getByCity(any());

        assertEquals(23., weatherTemperatureResponse.getTemperature());
    }
}
