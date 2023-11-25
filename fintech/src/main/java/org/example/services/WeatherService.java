package org.example.services;

import org.example.entities.Weather;

import java.util.List;

public interface WeatherService {
    Weather create(Weather weather);
    Weather createIfNewDate(Weather weather);
    Weather getById(Long id);
    Weather getByCity(String city);
    List<Weather> getAll();
    List<Weather> getTop30ByDate(String city);
    Weather update(Weather weather);
    void delete(Long id);
}
