package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.CacheAdapterFactory;
import com.hyd.simplecache.CacheConfiguration;

public class EhCacheConfiguration implements CacheConfiguration {

    static {
        CacheAdapterFactory.register(
                EhCacheConfiguration.class,
                conf -> new EhCacheAdapter((EhCacheConfiguration) conf)
        );
    }

    private int resourcePoolSize = 1000;

    private int timeToLiveSeconds = 3600;

    public int getResourcePoolSize() {
        return resourcePoolSize;
    }

    public void setResourcePoolSize(int resourcePoolSize) {
        this.resourcePoolSize = resourcePoolSize;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
