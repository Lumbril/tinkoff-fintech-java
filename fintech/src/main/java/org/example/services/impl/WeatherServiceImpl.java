package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.entities.City;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WeatherServiceImpl implements WeatherService {
    private WeatherRepository weatherRepository;

    private CityServiceImpl cityService;

    private WeatherTypeServiceImpl weatherTypeService;

    public WeatherServiceImpl(WeatherRepository weatherRepository,
                              CityServiceImpl cityService,
                              WeatherTypeServiceImpl weatherTypeService) {
        this.weatherRepository = weatherRepository;
        this.cityService = cityService;
        this.weatherTypeService = weatherTypeService;
    }

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
        List<Weather> weathers = new LinkedList<>();
        Iterable<Weather> iterable = weatherRepository.findAll();

        iterable.forEach(weathers::add);

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
