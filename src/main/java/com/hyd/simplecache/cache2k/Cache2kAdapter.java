package com.hyd.simplecache.cache2k;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.concurrent.TimeUnit;

public class Cache2kAdapter implements CacheAdapter {

    private Cache2kConfiguration configuration;

    private Cache<Object, Object> cache;

    public Cache2kAdapter(Cache2kConfiguration configuration) {
        this.configuration = configuration;
        this.cache = buildCache();
    }

    @SuppressWarnings("unchecked")
    private Cache<Object, Object> buildCache() {
        return Cache2kBuilder.forUnknownTypes()
                .entryCapacity(configuration.getEntryCapacity())
                .expireAfterWrite(configuration.getExpireAfterWriteMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Object get(String key) {
        return this.cache.get(key);
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
        this.cache.remove(key);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public void dispose() {
        this.cache.clearAndClose();
    }

    @Override
    public boolean keyExists(String key) {
        return this.cache.containsKey(key);
    }

    @Override
    public void touch(String key) {
        // not supported
    }
}
