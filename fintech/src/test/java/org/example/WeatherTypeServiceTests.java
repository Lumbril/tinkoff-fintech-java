package org.example;

import org.example.entities.WeatherType;
import org.example.repositories.WeatherTypeRepository;
import org.example.services.impl.WeatherTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WeatherTypeServiceTests {
    @Mock
    private WeatherTypeRepository weatherTypeRepository;

    @InjectMocks
    private WeatherTypeServiceImpl weatherTypeService;

    @Test
    public void getByIdStubTest() {
        Mockito.doReturn(Optional.of(new WeatherType(1L, "Sunny"))).when(weatherTypeRepository).findById(1L);

        WeatherType weatherType = weatherTypeService.getById(1L);

        assertEquals("Sunny", weatherType.getType());
    }

    @Test
    public void getByIdAndGetAllSpyTest() {
        Mockito.doReturn(Optional.of(new WeatherType(1L, "Sunny"))).when(weatherTypeRepository).findById(1L);

        WeatherType weatherType = weatherTypeService.getById(1L);
        assertEquals("Sunny", weatherType.getType());

        List<WeatherType> weatherTypes = weatherTypeService.getAll();
        assertEquals(0, weatherTypes.size());
    }
}
