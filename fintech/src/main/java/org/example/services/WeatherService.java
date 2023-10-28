package org.example.services;

import org.example.entities.Weather;

import java.util.List;

public interface WeatherService {
    Weather create(Weather weather);
    Weather getById(Long id);
    List<Weather> getAll();
    Weather update(Weather weather);
    void delete(Long id);
}
