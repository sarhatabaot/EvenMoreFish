package com.oheers.fish.database.data.manager;


import com.oheers.fish.database.data.strategy.DataSavingStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DataManager<T> {
    private final Map<String, T> cache = new ConcurrentHashMap<>();
    private final DataSavingStrategy<T> savingStrategy;
    private final Function<String, T> defaultLoader;

    // Constructor with default loader
    public DataManager(DataSavingStrategy<T> savingStrategy, Function<String, T> defaultLoader) {
        this.savingStrategy = savingStrategy;
        this.defaultLoader = defaultLoader;
    }

    // Get data with explicit loader
    public T get(String key, Function<String, T> loader) {
        return cache.computeIfAbsent(key, k -> {
            T data = loader.apply(k);
            savingStrategy.save(data);
            return data;
        });
    }

    public T getOrCreate(String key, Function<String, T> loader, T fallbackValue) {
        // First try to get with the provided loader
        T loadedValue = get(key, loader);

        if (loadedValue != null) {
            return loadedValue;
        }

        // If we got null, check if we have a cached value
        T cachedValue = cache.get(key);
        if (cachedValue != null) {
            return cachedValue;
        }

        // Otherwise, use the fallback value
        cache.put(key, fallbackValue);
        savingStrategy.save(fallbackValue);
        return fallbackValue;
    }

    // Get data using default loader, with a fallback value
    public T getOrCreate(String key, T fallbackValue) {
        if (defaultLoader == null) {
            throw new IllegalStateException("No default loader configured");
        }
        return getOrCreate(key, defaultLoader, fallbackValue);
    }
    

    // Get data using default loader
    public T get(String key) {
        if (defaultLoader == null) {
            throw new IllegalStateException("No default loader configured");
        }
        return get(key, defaultLoader);
    }

    // Update data (marks dirty and triggers save)
    public void update(String key, T data) {
        cache.put(key, data);
        savingStrategy.save(data);
    }

    // Batch update
    public void updateAll(Map<String, T> data) {
        cache.putAll(data);
        savingStrategy.saveAll(data.values());
    }

    // Periodic flush (for hybrid/periodic strategies)
    public void flush() {
        savingStrategy.saveAll(cache.values());
    }
}