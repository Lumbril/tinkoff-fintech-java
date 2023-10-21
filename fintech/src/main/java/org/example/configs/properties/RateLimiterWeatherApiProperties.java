package org.example.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weatherapi.ratelimiter")
@Getter
@Setter
public class RateLimiterWeatherApiProperties {
    private int limitForPeriod;
    private int limitRefreshPeriod;
    private int timeoutDuration;
}
