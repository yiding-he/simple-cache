package com.hyd.simplecache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;

import java.util.concurrent.TimeUnit;

public class CaffeineAdapter implements CacheAdapter {

    private CaffeineConfiguration configuration;

    private Cache<Object, Object> cache;

    public CaffeineAdapter(CaffeineConfiguration configuration) {
        this.configuration = configuration;
        this.cache = buildCache();
    }

    private Cache<Object, Object> buildCache() {

        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (configuration.getExpireAfterAccessMillis() > 0) {
            builder.expireAfterAccess(configuration.getExpireAfterAccessMillis(), TimeUnit.MILLISECONDS);
        }

        if (configuration.getExpireAfterWriteMillis() > 0) {
            builder.expireAfterWrite(configuration.getExpireAfterWriteMillis(), TimeUnit.MILLISECONDS);
        }

        if (configuration.getMaximumSize() > 0) {
            builder.maximumSize(configuration.getMaximumSize());
        }

        if (configuration.isSoftValues()) {
            builder.softValues();
        }
        return builder.build();
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Object get(String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void put(String key, Object value, boolean forever) {
        this.cache.put(key, value);
    }

    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        this.cache.put(key, value);
    }

    @Override
    public void delete(String key) {
        this.cache.invalidate(key);
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public void dispose() {
        this.cache.invalidateAll();
        this.cache.cleanUp();
    }

    @Override
    public boolean keyExists(String key) {
        return get(key) != null;
    }

    @Override
    public void touch(String key) {
        // not supported
    }
}
