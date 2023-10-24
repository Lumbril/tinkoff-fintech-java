package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.entities.Weather;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;

    @Override
    public Weather create(Weather weather) {
        return weatherRepository.save(weather);
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
