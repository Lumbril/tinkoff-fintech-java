package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.entities.WeatherType;
import org.example.repositories.WeatherTypeRepository;
import org.example.services.WeatherTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeatherTypeServiceImpl implements WeatherTypeService {
    private final WeatherTypeRepository weatherTypeRepository;

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
        List<WeatherType> weatherTypes = weatherTypeRepository.findAll();

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
