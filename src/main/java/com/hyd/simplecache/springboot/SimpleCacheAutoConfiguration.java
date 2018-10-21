package com.hyd.simplecache.springboot;

import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.RedisConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "simple-cache")
public class SimpleCacheAutoConfiguration {

    private Map<String, RedisConfiguration> redis = new HashMap<>();

    private Map<String, MemcachedConfiguration> memcached = new HashMap<>();

    public Map<String, MemcachedConfiguration> getMemcached() {
        return memcached;
    }

    public void setMemcached(Map<String, MemcachedConfiguration> memcached) {
        this.memcached = memcached;
    }

    public Map<String, RedisConfiguration> getRedis() {
        return redis;
    }

    public void setRedis(Map<String, RedisConfiguration> redis) {
        this.redis = redis;
    }
}
