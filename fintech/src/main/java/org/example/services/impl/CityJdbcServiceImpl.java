package org.example.services.impl;

import org.example.entities.City;
import org.example.entities.WeatherType;
import org.example.services.CityService;
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
public class CityJdbcServiceImpl implements CityService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public City create(City city) {
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO city (city) VALUES (?)",
                    new String[]{"id"}
            );
            ps.setString(1, city.getCity());

            return ps;
        };
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        Long id = keyHolder.getKey().longValue();

        City cityCreated = getById(id);

        return cityCreated;
    }

    @Override
    public City getById(Long id) {
        City city = jdbcTemplate.queryForObject(
                "SELECT * FROM city WHERE id = (?)",
                new Object[]{id},
                (rs, rowNum) -> getCity(rs)
        );

        return city;
    }

    @Override
    public List<City> getAll() {
        List<City> cities = jdbcTemplate.query(
                "SELECT * FROM city",
                (rs, rowNum) -> getCity(rs)
        );

        return cities;
    }

    @Override
    public City update(City city) {
        jdbcTemplate.update(
                "UPDATE city SET city = ? WHERE id = ?",
                city.getCity(),
                city.getId()
        );

        City cityUpdated = getById(city.getId());

        return cityUpdated;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM city WHERE id = (?)", id);
    }

    private City getCity(ResultSet rs) throws SQLException {
        return City.builder()
                .id(rs.getLong("id"))
                .city(rs.getString("city"))
                .build();
    }
}
