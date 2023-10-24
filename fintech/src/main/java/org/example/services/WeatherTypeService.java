package org.example.services;

import org.example.entities.WeatherType;

import java.util.List;

public interface WeatherTypeService {
    WeatherType create(WeatherType weatherType);
    WeatherType getById(Long id);
    List<WeatherType> getAll();
    WeatherType update(WeatherType weatherType);
    void delete(Long id);
}
