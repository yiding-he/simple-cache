package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.EhCacheConfiguration;
import com.hyd.simplecache.SimpleCache;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建 EhCache 缓存的工厂类
 *
 * @author 贺一丁
 */
public class EhCacheFactory {

    private static final Map<String, SimpleCache> CACHE_MAP = new HashMap<String, SimpleCache>();

    /**
     * 获取或创建 EhCache 缓存
     *
     * @param namespace 命名空间
     *
     * @return EhCache 缓存
     */
    public static SimpleCache getOrCreateCache(String namespace) {
        if (CACHE_MAP.containsKey(namespace)) {
            return CACHE_MAP.get(namespace);
        }

        synchronized (CACHE_MAP) {
            if (CACHE_MAP.containsKey(namespace)) {
                return CACHE_MAP.get(namespace);
            }

            SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
            CACHE_MAP.put(namespace, cache);

            return cache;
        }
    }
}
