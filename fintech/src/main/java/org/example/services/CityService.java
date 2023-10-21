package org.example.services;

import org.example.entities.City;

import java.util.List;

public interface CityService {
    City create(City city);
    City getById(Long id);
    City getByCity(String city);
    List<City> getAll();
    City update(City city);
    void delete(Long id);
}
