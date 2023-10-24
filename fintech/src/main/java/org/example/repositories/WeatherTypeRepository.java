package org.example.repositories;

import org.example.entities.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherTypeRepository extends JpaRepository<WeatherType, Long> {
}
