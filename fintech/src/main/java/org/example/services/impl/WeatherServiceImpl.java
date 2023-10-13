package org.example.services.impl;

import org.example.entities.Weather;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Autowired
    private WeatherRepository weatherRepository;

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
