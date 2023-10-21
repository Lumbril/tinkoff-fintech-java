package org.example.repositories;

import org.example.entities.WeatherType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherTypeRepository extends CrudRepository<WeatherType, Long> {
    Optional<WeatherType> findByType(String type);
}
