package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;

import java.time.Duration;

/**
 * 使用 EhCache 实现的缓存包装。
 *
 * 注意 EhCache 不支持为单个 key 设置超时时间，所以
 * {@link #put(String, Object, boolean)} 和 {@link #put(String, Object, int)}
 * 的最后一个参数都是无效的。
 */
public class EhCacheAdapter implements CacheAdapter {

    private static final CacheManager CACHE_MANAGER;

    static {
        CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build();
        CACHE_MANAGER.init();
    }

    private EhCacheConfiguration configuration;

    private final Cache<String, Object> cache;

    public EhCacheAdapter(EhCacheConfiguration configuration) {
        this.configuration = configuration;

        ResourcePools resourcePools = ResourcePoolsBuilder.heap(configuration.getResourcePoolSize()).build();

        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder
                .timeToLiveExpiration(Duration.ofSeconds(configuration.getTimeToLiveSeconds()));

        org.ehcache.config.CacheConfiguration<String, Object> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, Object.class, resourcePools)
                .withExpiry(expiry)
                .build();

        cache = CACHE_MANAGER.createCache(configuration.getAlias(), cacheConfiguration);
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    // last parameter not supported by EhCache
    @Override
    public void put(String key, Object value, boolean forever) {
        cache.put(key, value);
    }

    // last parameter not supported by EhCache
    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        cache.put(key, value);
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void dispose() {
        cache.clear();
    }

    @Override
    public boolean keyExists(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void touch(String key) {

    }
}
