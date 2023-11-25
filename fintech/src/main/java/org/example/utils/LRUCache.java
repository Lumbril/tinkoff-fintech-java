package org.example.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class LRUCache<K, V> {
    private final int maxSize;
    private final Map<K, V> cache;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
        Map<K, V> b = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };

        this.cache = Collections.synchronizedMap(b);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public V update(K key, V newValue) {
        return cache.put(key, newValue);
    }

    public void remove(K key) {
        cache.remove(key);
    }
}
