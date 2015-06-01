package com.hyd.simplecache.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 创建各种 Map 缓存
 *
 * @author HeYiding
 */
public class MapCacheUtils {

    /**
     * 创建一个 LRU 缓存
     *
     * @param size       缓存大小
     * @param threadSafe 是否要做线程同步
     *
     * @return LRU 缓存
     */
    public static Map newLRUCache(final int size, boolean threadSafe) {

        Map cache = new LinkedHashMap(size + 1, .75F, true) {

            // This method is called just after a new entry has been added
            @Override
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > size;
            }
        };

        return threadSafe ? Collections.synchronizedMap(cache) : cache;
    }
}
