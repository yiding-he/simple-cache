package com.hyd.simplecache.cache2k;

import com.hyd.simplecache.CacheAdapterFactory;
import com.hyd.simplecache.CacheConfiguration;

public class Cache2kConfiguration implements CacheConfiguration {

    static {
        CacheAdapterFactory.register(
                Cache2kConfiguration.class,
                config -> new Cache2kAdapter((Cache2kConfiguration) config)
        );
    }

    private long entryCapacity = 10000;

    private long expireAfterWriteMillis = 3600000;

    public long getEntryCapacity() {
        return entryCapacity;
    }

    public void setEntryCapacity(long entryCapacity) {
        this.entryCapacity = entryCapacity;
    }

    public long getExpireAfterWriteMillis() {
        return expireAfterWriteMillis;
    }

    public void setExpireAfterWriteMillis(long expireAfterWriteMillis) {
        this.expireAfterWriteMillis = expireAfterWriteMillis;
    }
}
