package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Weather;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherTemperatureResponse {
    @JsonProperty("temperature")
    private Double temperature;

    public WeatherTemperatureResponse(Weather weather) {
        this.temperature = weather.getTemperature();
    }
}
