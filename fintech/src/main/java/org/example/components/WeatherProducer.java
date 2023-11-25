package org.example.components;

import lombok.RequiredArgsConstructor;
import org.example.dto.WeatherDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherProducer {
    private static final String TOPIC = "weather-topic";
    private final KafkaTemplate<String, WeatherDto> kafkaTemplate;

    public void send(WeatherDto weatherDto) {
        kafkaTemplate.send(TOPIC, weatherDto);
    }
}
