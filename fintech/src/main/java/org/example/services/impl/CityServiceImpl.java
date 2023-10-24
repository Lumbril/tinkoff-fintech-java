package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.entities.City;
import org.example.repositories.CityRepository;
import org.example.services.CityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public City create(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City getById(Long id) {
        return cityRepository.findById(id).orElseThrow();
    }

    @Override
    public List<City> getAll() {
        List<City> cities = cityRepository.findAll();

        return cities;
    }

    @Override
    public City update(City city) {
        if (!cityRepository.existsById(city.getId())) {
            throw new NoSuchElementException("No value present");
        }

        return cityRepository.save(city);
    }

    @Override
    public void delete(Long id) {
        City city = getById(id);

        cityRepository.delete(city);
    }
}
