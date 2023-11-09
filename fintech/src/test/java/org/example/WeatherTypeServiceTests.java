package org.example;

import org.example.entities.WeatherType;
import org.example.repositories.WeatherTypeRepository;
import org.example.services.impl.WeatherTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class WeatherTypeServiceTests {
    @Test
    public void getByIdStubTest() {
        WeatherTypeRepository weatherTypeRepository = Mockito.mock(WeatherTypeRepository.class);
        WeatherTypeServiceImpl weatherTypeService = new WeatherTypeServiceImpl(weatherTypeRepository);
        Mockito.doReturn(Optional.of(new WeatherType(1L, "Sunny"))).when(weatherTypeRepository).findById(1L);

        WeatherType weatherType = weatherTypeService.getById(1L);

        assertEquals("Sunny", weatherType.getType());
    }

    @Test
    public void getByIdAndGetAllSpyTest() {
        WeatherTypeRepository weatherTypeRepository = Mockito.spy(WeatherTypeRepository.class);
        WeatherTypeServiceImpl weatherTypeService = new WeatherTypeServiceImpl(weatherTypeRepository);
        Mockito.doReturn(Optional.of(new WeatherType(1L, "Sunny"))).when(weatherTypeRepository).findById(1L);

        WeatherType weatherType = weatherTypeService.getById(1L);
        assertEquals("Sunny", weatherType.getType());

        List<WeatherType> weatherTypes = weatherTypeService.getAll();
        assertEquals(0, weatherTypes.size());
    }
}
