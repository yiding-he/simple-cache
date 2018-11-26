package com.hyd.simplecache.springboot;

import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SimpleCacheFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCacheFactory.class);

    private Map<String, SimpleCache> simpleCacheMappings = new HashMap<String, SimpleCache>();

    public SimpleCacheFactory(SimpleCacheAutoConfiguration configuration) {

        Map<String, RedisConfiguration> redisConfMap = configuration.getRedis();
        if (redisConfMap != null) {
            for (Map.Entry<String, RedisConfiguration> entry : redisConfMap.entrySet()) {
                this.register(entry.getKey(), entry.getValue());
            }
        }

        Map<String, MemcachedConfiguration> mcConfMap = configuration.getMemcached();
        if (mcConfMap != null) {
            for (Map.Entry<String, MemcachedConfiguration> entry : mcConfMap.entrySet()) {
                this.register(entry.getKey(), entry.getValue());
            }
        }
    }

    private void register(String name, CacheConfiguration config) {
        if (simpleCacheMappings.containsKey(name)) {
            throw new AutoConfigurationException("Cache name '" + name + "' already exists.");
        }

        simpleCacheMappings.put(name, new SimpleCache(config));
        LOG.info("Cache '" + name + "' registered.");
    }

    public SimpleCache getSimpleCache(String name) {
        return simpleCacheMappings.get(name);
    }
}
