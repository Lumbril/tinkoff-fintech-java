package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private Long id;
    private String regionName;
    private Integer temperature;
    private LocalDateTime date;

    @Override
    public String toString() {
        return "{" +
                id + ", " +
                regionName + ", " +
                temperature + ", " +
                date +
                "}";
    }
}
