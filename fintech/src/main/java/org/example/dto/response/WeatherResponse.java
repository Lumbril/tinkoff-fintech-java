package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Weather;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("region_name")
    private String regionName;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("date")
    private LocalDateTime date;

    public WeatherResponse(Weather weather) {
        this.id = weather.getId();
        this.regionName = weather.getRegionName();
        this.temperature = weather.getTemperature();
        this.date = weather.getDate();
    }
}
