package com.intuit.support.hub.utils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache wrapper to be able to easily replace the usage with external cache like redis.
 */
public class Cache<K,V> {
    private Map<K, V> cache = new ConcurrentHashMap<>();

    public V get(K key) {
        return cache.get(key);
    }

    public V getOrDefault(K key, V defVal) {
        return cache.getOrDefault(key, defVal);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public Collection<V> getAll() {
        return cache.values();
    }

    public void clear() {
        cache.clear();
    }
}
