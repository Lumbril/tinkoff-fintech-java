package org.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {
    @NotNull
    @JsonProperty(value = "temperature", required = true)
    private Double temperature;

    @NotNull
    @JsonProperty(value = "date", required = true)
    private LocalDateTime date;
}
