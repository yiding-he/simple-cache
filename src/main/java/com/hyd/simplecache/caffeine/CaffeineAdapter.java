package com.hyd.simplecache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.CaffeineConfiguration;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class CaffeineAdapter implements CacheAdapter {

    private CaffeineConfiguration configuration;

    private Cache<String, Object> cache;

    public CaffeineAdapter(CaffeineConfiguration configuration) {
        this.configuration = configuration;
        this.cache = buildCaffeine();
    }

    private Cache<String, Object> buildCaffeine() {
        return Caffeine.newBuilder()
                .expireAfterWrite(configuration.getTtlSeconds(), TimeUnit.SECONDS)
                .maximumSize(configuration.getMaxEntries())
                .build();
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(String key, Object value, boolean forever) {
        if (value != null) {
            cache.put(key, value);
        }
    }

    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        if (value != null) {
            cache.put(key, value);
        }
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public void dispose() {
        clear();
    }

    @Override
    public Iterator<String> keys() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean keyExists(String key) {
        return get(key) != null;
    }

    @Override
    public void touch(String key) {

    }
}
