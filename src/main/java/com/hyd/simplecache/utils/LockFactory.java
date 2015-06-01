package com.hyd.simplecache.utils;

import java.util.Map;

/**
 * 根据 key 获得锁，相同的 key 将获得相同的锁
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class LockFactory {

    public static final int CACHE_SIZE = 100000;

    private static final Map<Integer, Object> CACHE = MapCacheUtils.newLRUCache(CACHE_SIZE, false);

    public static synchronized Object getLock(String cacheKey) {
        Object lock;
        int hash = cacheKey.hashCode();

        if (!CACHE.containsKey(hash)) {
            lock = new Object();
            CACHE.put(hash, lock);
        } else {
            lock = CACHE.get(hash);
        }

        return lock;
    }
}
