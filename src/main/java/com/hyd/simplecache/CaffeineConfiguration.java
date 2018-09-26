package com.hyd.simplecache;

public class CaffeineConfiguration implements CacheConfiguration {

    private int maxEntries = 10000;

    private int ttlSeconds = 3600;

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
