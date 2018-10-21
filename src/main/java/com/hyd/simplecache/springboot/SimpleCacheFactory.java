package com.hyd.simplecache.springboot;

import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SimpleCacheFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCacheFactory.class);

    private Map<String, SimpleCache> simpleCacheMappings = new HashMap<>();

    public SimpleCacheFactory(SimpleCacheAutoConfiguration configuration) {
        if (configuration.getRedis() != null) {
            configuration.getRedis().forEach(this::register);
        }

        if (configuration.getMemcached() != null) {
            configuration.getMemcached().forEach(this::register);
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
