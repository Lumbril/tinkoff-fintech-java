package org.example;

import org.example.entities.City;
import org.example.entities.Weather;
import org.example.entities.WeatherType;
import org.example.services.CacheService;
import org.example.services.CityService;
import org.example.services.WeatherService;
import org.example.services.WeatherTypeService;
import org.example.services.impl.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //@Bean
    public CommandLineRunner testJpa(WeatherTypeServiceImpl weatherTypeService,
                                     CityServiceImpl cityService,
                                     WeatherServiceImpl weatherService) {
        return args -> {
            System.out.println("TESTING JPA\n");
            testWeatherType(weatherTypeService);
            testCity(cityService);
            testWeather(weatherService, weatherTypeService, cityService);
        };
    }

    //@Bean
    public CommandLineRunner testJdbc(WeatherTypeJdbcServiceImpl weatherTypeService,
                                      CityJdbcServiceImpl cityService,
                                      WeatherJdbcServiceImpl weatherService) {
        return args -> {
            System.out.println("TESTING JDBC\n");
            testWeatherType(weatherTypeService);
            testCity(cityService);
            testWeather(weatherService, weatherTypeService, cityService);
        };
    }

    private void testWeatherType(WeatherTypeService weatherTypeService) {
        System.out.println("WEATHER TYPES");
        System.out.println(weatherTypeService.create(
                WeatherType.builder()
                        .type("Сильный дождь")
                        .build()
        ));
        System.out.println(weatherTypeService.getById(1L));
        System.out.println(weatherTypeService.getAll());

        WeatherType weatherTypeForUpd = weatherTypeService.getById(6L);
        weatherTypeForUpd.setType("Сильный дождь UPD");
        System.out.println(weatherTypeService.update(weatherTypeForUpd));

        weatherTypeService.delete(6L);
        System.out.println(weatherTypeService.getAll());
    }

    private void testCity(CityService cityService) {
        System.out.println("\nCITIES");
        System.out.println(cityService.create(
                City.builder()
                        .city("Москва")
                        .build()
        ));
        System.out.println(cityService.create(
                City.builder()
                        .city("Казань")
                        .build()
        ));
        System.out.println(cityService.create(
                City.builder()
                        .city("Уфа")
                        .build()
        ));
        System.out.println(cityService.getById(1L));
        System.out.println(cityService.getAll());

        City cityForUpd = cityService.getById(2L);
        cityForUpd.setCity("Казань UPD");
        System.out.println(cityService.update(cityForUpd));

        cityService.delete(2L);
        System.out.println(cityService.getAll());
    }

    private void testWeather(WeatherService weatherService,
                             WeatherTypeService weatherTypeService,
                             CityService cityService) {
        System.out.println("\nWEATHERS");
        System.out.println(weatherService.create(
                Weather.builder()
                        .weatherType(weatherTypeService.getById(1L))
                        .city(cityService.getById(1L))
                        .temperature(25.6D)
                        .dateTime(LocalDateTime.now())
                        .build()
        ));
        System.out.println(weatherService.create(
                Weather.builder()
                        .weatherType(weatherTypeService.getById(4L))
                        .city(cityService.getById(3L))
                        .temperature(13.D)
                        .dateTime(LocalDateTime.now())
                        .build()
        ));
        System.out.println(weatherService.getById(2L));
        System.out.println(weatherService.getAll());

        Weather weatherForUpd = weatherService.getById(2L);
        weatherForUpd.setTemperature(11.D);
        System.out.println(weatherService.update(weatherForUpd));

        weatherService.delete(2L);
        System.out.println(weatherService.getAll());
    }
}
