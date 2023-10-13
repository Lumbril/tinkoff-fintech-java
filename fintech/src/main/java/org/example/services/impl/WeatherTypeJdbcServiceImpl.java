package org.example.services.impl;

import org.example.entities.WeatherType;
import org.example.services.WeatherTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class WeatherTypeJdbcServiceImpl implements WeatherTypeService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public WeatherType create(WeatherType weatherType) {
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO weather_type (type) VALUES (?)",
                    new String[]{"id"}
            );
            ps.setString(1, weatherType.getType());

            return ps;
        };
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        Long id = keyHolder.getKey().longValue();

        WeatherType weatherTypeCreated = getById(id);

        return weatherTypeCreated;
    }

    @Override
    public WeatherType getById(Long id) {
        WeatherType weatherType = jdbcTemplate.queryForObject(
                "SELECT * FROM weather_type WHERE id = (?)",
                new Object[]{id},
                (rs, rowNum) -> getWeatherType(rs)
        );

        return weatherType;
    }

    @Override
    public List<WeatherType> getAll() {
        List<WeatherType> weatherTypes = jdbcTemplate.query(
                "SELECT * FROM weather_type",
                (rs, rowNum) -> getWeatherType(rs)
        );

        return weatherTypes;
    }

    @Override
    public WeatherType update(WeatherType weatherType) {
        jdbcTemplate.update(
                "UPDATE weather_type SET type = ? WHERE id = ?",
                weatherType.getType(),
                weatherType.getId()
        );

        WeatherType weatherTypeUpdated = getById(weatherType.getId());

        return weatherTypeUpdated;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM weather_type WHERE id = (?)", id);
    }

    private WeatherType getWeatherType(ResultSet rs) throws SQLException {
        return WeatherType.builder()
                .id(rs.getLong("id"))
                .type(rs.getString("type"))
                .build();
    }
}
