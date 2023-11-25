package org.example.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.WeatherDto;
import org.example.entities.Weather;
import org.example.services.impl.CityServiceImpl;
import org.example.services.impl.WeatherServiceImpl;
import org.example.services.impl.WeatherTypeServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherConsumer {
    private final WeatherServiceImpl weatherService;
    private final CityServiceImpl cityService;
    private final WeatherTypeServiceImpl weatherTypeService;

    @KafkaListener(topics = "weather-topic", groupId = "weather-group")
    public void listenWeatherDto(WeatherDto weatherDto) {
        log.info(weatherDto.toString());

        weatherService.createIfNewDate(Weather.builder()
                        .city(cityService.getByCityOrCreate(weatherDto.getCity()))
                        .weatherType(weatherTypeService.getByTypeOrCreate(weatherDto.getType()))
                        .temperature(weatherDto.getTemperature())
                        .dateTime(weatherDto.getDateTime())
                .build());

        List<Weather> weathers = weatherService.getTop30ByDate(weatherDto.getCity());

        log.info("Moving avg for " + weatherDto.getCity() + ": " + calculateMovingAverage(weathers));
    }

    private double calculateMovingAverage(List<Weather> weathers) {
        double sum = 0;

        for (Weather w : weathers) {
            sum += w.getTemperature();
        }

        return sum / weathers.size();
    }
}
