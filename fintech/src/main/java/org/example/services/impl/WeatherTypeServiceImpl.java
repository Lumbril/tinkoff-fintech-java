package org.example.services.impl;

import org.example.entities.WeatherType;
import org.example.repositories.WeatherTypeRepository;
import org.example.services.WeatherTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WeatherTypeServiceImpl implements WeatherTypeService {
    @Autowired
    private WeatherTypeRepository weatherTypeRepository;

    @Override
    public WeatherType create(WeatherType weatherType) {
        return weatherTypeRepository.save(weatherType);
    }

    @Override
    public WeatherType getById(Long id) {
        return weatherTypeRepository.findById(id).orElseThrow();
    }

    @Override
    public List<WeatherType> getAll() {
        List<WeatherType> weatherTypes = new LinkedList<>();
        Iterable<WeatherType> iterable = weatherTypeRepository.findAll();

        iterable.forEach(weatherTypes::add);

        return weatherTypes;
    }

    @Override
    public WeatherType update(WeatherType weatherType) {
        if (!weatherTypeRepository.existsById(weatherType.getId())) {
            throw new NoSuchElementException("No value present");
        }

        return weatherTypeRepository.save(weatherType);
    }

    @Override
    public void delete(Long id) {
        WeatherType weatherType = getById(id);

        weatherTypeRepository.delete(weatherType);
    }
}
