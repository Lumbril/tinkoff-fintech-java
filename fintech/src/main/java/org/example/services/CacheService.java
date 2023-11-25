package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.entities.Weather;
import org.example.utils.LRUCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    private static LRUCache<String, Weather> CACHE;

    @Value("${cache.course.size}")
    public void setSizeCache(int size) {
        CACHE = new LRUCache<>(size);
    }

    public Weather get(String key) {
        return CACHE.get(key);
    }

    public Weather update(String key, Weather newWeather) {
        return CACHE.update(key, newWeather);
    }

    public void remove(String key) {
        CACHE.remove(key);
    }
}
