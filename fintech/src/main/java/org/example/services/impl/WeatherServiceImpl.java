package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.entities.City;
import lombok.RequiredArgsConstructor;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Override
    public Weather createIfNewDate(Weather weather) {
        Optional<Weather> w = weatherRepository.findByCity_CityAndDateTime(weather.getCity().getCity(), weather.getDateTime());

        if (w.isPresent()) {
            return null;
        } else {
            return create(weather);
        }
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
                .dateTime(Instant.ofEpochSecond(Long.valueOf(jsonNode.get("current").get("last_updated_epoch").toString()))
                        .atZone(ZoneId.of("UTC")).toLocalDateTime())
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
    public Weather getByCity(String city) {
        return weatherRepository.findByCity_City(city).orElse(null);
    }

    @Override
    public List<Weather> getAll() {
        List<Weather> weathers = weatherRepository.findAll();

        return weathers;
    }

    @Override
    public List<Weather> getTop30ByDate(String city) {
        return weatherRepository.findTop30ByCity_CityOrderByDateTimeDesc(city);
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
