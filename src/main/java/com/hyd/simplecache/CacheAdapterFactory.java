package com.hyd.simplecache;

import com.hyd.simplecache.ehcache.EhCacheAdapter;
import com.hyd.simplecache.memcached.MemcachedAdapter;
import com.hyd.simplecache.redis.RedisAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建 CacheAdapter 对象的工厂类。
 *
 * @author 贺一丁
 */
public class CacheAdapterFactory {

    private static final Map<Class<? extends CacheConfiguration>, CacheAdapterCreator>
            CREATOR_MAP = new HashMap<Class<? extends CacheConfiguration>, CacheAdapterCreator>();

    /**
     * 如果有新的缓存类型需要创建，首先为该类型实现一个
     * {@link CacheAdapterCreator} 类，然后将其加入下面这个 Map 当中
     */
    static {
        CREATOR_MAP.put(EhCacheConfiguration.class, new EhCacheCreator());
        CREATOR_MAP.put(MemcachedConfiguration.class, new MemcachedCreator());
        CREATOR_MAP.put(RedisConfiguration.class, new RedisCreator());
    }

    /**
     * 创建 CacheAdapter 对象
     *
     * @param configuration 缓存配置
     *
     * @return 创建的 CacheAdapter 对象
     */
    @SuppressWarnings("unchecked")
    public static CacheAdapter createCacheAdapter(CacheConfiguration configuration) {

        for (Class<? extends CacheConfiguration> cacheConfigType : CREATOR_MAP.keySet()) {

            boolean isCompatibleType = cacheConfigType.isAssignableFrom(configuration.getClass());

            if (isCompatibleType) {
                CacheAdapterCreator adapterCreator = CREATOR_MAP.get(cacheConfigType);
                return adapterCreator.create(configuration);
            }
        }

        throw new IllegalArgumentException("Unrecognized configuration type: " + configuration.getClass());
    }

    ///////////////////////////////////////////////////////////////

    private static interface CacheAdapterCreator<T extends CacheConfiguration> {

        CacheAdapter create(T configuration);
    }

    /**
     * EhCache 缓存创建类
     */
    private static class EhCacheCreator implements CacheAdapterCreator<EhCacheConfiguration> {

        @Override
        public CacheAdapter create(EhCacheConfiguration configuration) {
            return new EhCacheAdapter(configuration);
        }
    }

    /**
     * Memcached 缓存创建类
     */
    private static class MemcachedCreator implements CacheAdapterCreator<MemcachedConfiguration> {

        @Override
        public CacheAdapter create(MemcachedConfiguration configuration) {
            return new MemcachedAdapter(configuration);
        }
    }

    private static class RedisCreator implements CacheAdapterCreator<RedisConfiguration> {

        @Override
        public CacheAdapter create(RedisConfiguration configuration) {
            return new RedisAdapter(configuration);
        }
    }
}
