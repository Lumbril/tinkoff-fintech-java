package org.example.services.impl;

import org.example.entities.City;
import org.example.repositories.CityRepository;
import org.example.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    private CityRepository cityRepository;

    @Override
    public City create(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City getById(Long id) {
        return cityRepository.findById(id).orElseThrow();
    }

    @Override
    public City getByCity(String city) {
        return cityRepository.findCityByCity(city).orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public City getByCityOrCreate(String city) {
        City c = getByCity(city);

        return c != null ? c : create(City.builder().city(city).build());
    }

    @Override
    public List<City> getAll() {
        List<City> cities = new LinkedList<>();
        Iterable<City> iterable = cityRepository.findAll();

        iterable.forEach(cities::add);

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
