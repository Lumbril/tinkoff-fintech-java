package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.entities.City;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.services.WeatherService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeatherJdbcServiceImpl implements WeatherService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Weather create(Weather weather) {
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO weather (temperature, city_id, weather_type_id, date) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setDouble(1, weather.getTemperature());
            ps.setLong(2, weather.getCity().getId());
            ps.setLong(3, weather.getWeatherType().getId());
            ps.setString(4, weather.getDateTime().toString());

            return ps;
        };

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKey().longValue();
        Weather weatherCreated = getById(id);

        return weatherCreated;
    }

    @Override
    public Weather getById(Long id) {
        try {
            Weather weather = jdbcTemplate.queryForObject(
                    "SELECT w.id AS weather_id, w.temperature, w.date, " +
                            "c.id AS city_id, c.city, " +
                            "wt.id AS weather_type_id, wt.type " +
                            "FROM weather AS w " +
                            "LEFT JOIN city AS c ON w.city_id = c.id " +
                            "LEFT JOIN weather_type AS wt ON w.weather_type_id = wt.id " +
                            "WHERE w.id = (?)",
                    new Object[]{id},
                    (rs, rowNum) -> getWeather(rs)
            );

            return weather;
        } catch (DataAccessException e) {
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public List<Weather> getAll() {
        List<Weather> weathers = jdbcTemplate.query(
                "SELECT w.id AS weather_id, w.temperature, w.date, " +
                "c.id AS city_id, c.city, " +
                "wt.id AS weather_type_id, wt.type " +
                "FROM weather AS w " +
                "LEFT JOIN city AS c ON w.city_id = c.id " +
                "LEFT JOIN weather_type AS wt ON w.weather_type_id = wt.id",
                (rs, rowNum) -> getWeather(rs)
        );

        return weathers;
    }

    @Override
    public Weather update(Weather weather) {
        jdbcTemplate.update(
                "UPDATE weather SET temperature = ?, city_id = ?, weather_type_id = ?, date = ? " +
                        "WHERE id = ?",
                weather.getTemperature(),
                weather.getCity().getId(),
                weather.getWeatherType().getId(),
                weather.getDateTime(),
                weather.getId()
        );

        Weather weatherUpdated = getById(weather.getId());

        return weatherUpdated;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM weather WHERE id = (?)", id);
    }

    private Weather getWeather(ResultSet rs) throws SQLException {
        Weather w = new Weather();
        w.setId(rs.getLong("weather_id"));
        w.setTemperature(rs.getDouble("temperature"));
        w.setDateTime(
                LocalDateTime.parse(
                        rs.getString("date"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n")
                )
        );

        City city = new City();
        city.setId(rs.getLong("city_id"));
        city.setCity(rs.getString("city"));

        WeatherType weatherType = new WeatherType();
        weatherType.setId(rs.getLong("weather_type_id"));
        weatherType.setType(rs.getString("type"));

        w.setCity(city);
        w.setWeatherType(weatherType);

        return w;
    }
}
