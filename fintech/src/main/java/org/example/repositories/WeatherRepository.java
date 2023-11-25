package org.example.repositories;

import org.example.entities.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByCity_City(String city);
    Optional<Weather> findByCity_CityAndDateTime(String city, LocalDateTime dateTime);
    List<Weather> findTop30ByCity_CityOrderByDateTimeDesc(String city);
}
