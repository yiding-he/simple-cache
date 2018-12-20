package com.hyd.simplecache.springboot;

import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Caches {

    private static final Logger LOG = LoggerFactory.getLogger(Caches.class);

    private Map<String, SimpleCache> simpleCacheMappings = new HashMap<>();

    public Caches(SimpleCacheAutoConfiguration configuration) {
        register(configuration.getCache2k());
        register(configuration.getCaffeine());
        register(configuration.getEhcache());
        register(configuration.getMemcached());
        register(configuration.getRedis());
    }

    public void forEach(BiConsumer<String, SimpleCache> consumer) {
        this.simpleCacheMappings.forEach(consumer);
    }

    private void register(Map<String, ? extends CacheConfiguration> configs) {
        if (configs != null) {
            configs.forEach(this::register);
        }
    }

    private void register(String name, CacheConfiguration config) {
        if (simpleCacheMappings.containsKey(name)) {
            throw new AutoConfigurationException("Cache name '" + name + "' already exists.");
        }

        simpleCacheMappings.put(name, new SimpleCache(config));
        LOG.info("Cache '" + name + "'[" + config.getType() + "] created.");
    }

    public SimpleCache get(String name) {
        return simpleCacheMappings.get(name);
    }
}
