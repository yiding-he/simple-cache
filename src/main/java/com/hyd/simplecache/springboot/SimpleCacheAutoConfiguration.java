package com.hyd.simplecache.springboot;

import com.hyd.simplecache.cache2k.Cache2kConfiguration;
import com.hyd.simplecache.caffeine.CaffeineConfiguration;
import com.hyd.simplecache.memcached.MemcachedConfiguration;
import com.hyd.simplecache.redis.RedisConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "simple-cache")
@Data
public class SimpleCacheAutoConfiguration {

    private Map<String, RedisConfiguration> redis = new HashMap<>();

    private Map<String, MemcachedConfiguration> memcached = new HashMap<>();

    private Map<String, CaffeineConfiguration> caffeine = new HashMap<>();

    private Map<String, Cache2kConfiguration> cache2k = new HashMap<>();
}
