package org.example.configs;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.example.configs.properties.RateLimiterWeatherApiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    private final RateLimiterWeatherApiProperties rateLimiterProperties;

    public Resilience4jConfig(RateLimiterWeatherApiProperties rateLimiterProperties) {
        this.rateLimiterProperties = rateLimiterProperties;
    }

    @Bean
    @Qualifier("ratelimiterWeatherapi")
    public RateLimiter rateLimiter() {
        RateLimiterConfig.ofDefaults();

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rateLimiterProperties.getLimitForPeriod())
                .limitRefreshPeriod(Duration.ofDays(rateLimiterProperties.getLimitRefreshPeriod()))
                .timeoutDuration(Duration.ofSeconds(rateLimiterProperties.getTimeoutDuration()))
                .build();

        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

        return rateLimiterRegistry.rateLimiter("weatherapi");
    }
}
