package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.entities.City;
import lombok.RequiredArgsConstructor;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;

    private final CityServiceImpl cityService;

    private final WeatherTypeServiceImpl weatherTypeService;

    @Override
    public Weather create(Weather weather) {
        return weatherRepository.save(weather);
    }

    public Weather createFromJsonNode(JsonNode jsonNode) {
        City c = cityService.getByCityOrCreate(jsonNode.get("location").get("name")
                .toString()
                .replaceAll("\"", "")
        );

        WeatherType wt = weatherTypeService.getByTypeOrCreate(
                jsonNode
                        .get("current")
                        .get("condition")
                        .get("text")
                        .toString()
                        .replaceAll("\"", "")
        );

        Weather w = Weather.builder()
                .temperature(Double.valueOf(String.valueOf(jsonNode.get("current").get("temp_c"))))
                .dateTime(LocalDateTime.now())
                .weatherType(wt)
                .city(c)
                .build();

        return create(w);
    }

    @Override
    public Weather getById(Long id) {
        return weatherRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Weather> getAll() {
        List<Weather> weathers = weatherRepository.findAll();

        return weathers;
    }

    @Override
    public Weather update(Weather weather) {
        if (!weatherRepository.existsById(weather.getId())) {
            throw new NoSuchElementException("No value present");
        }

        return weatherRepository.save(weather);
    }

    @Override
    public void delete(Long id) {
        Weather weather = getById(id);

        weatherRepository.delete(weather);
    }
}
