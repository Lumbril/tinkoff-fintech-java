package org.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDto {
    private String city;
    private String type;
    private Double temperature;
    private LocalDateTime dateTime;
}
