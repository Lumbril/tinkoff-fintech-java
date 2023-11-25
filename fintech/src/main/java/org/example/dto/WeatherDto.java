package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WeatherDto {
    private String city;
    private String type;
    private Double temperature;
    private LocalDateTime dateTime;
}
