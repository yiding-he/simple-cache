package com.hyd.simplecache;

import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class MemcachedAdapterTest {

    @Test
    public void testConnectionPool() throws Exception {
        MemcachedConfiguration config =
                new MemcachedConfiguration("woplus.content", "localhost:11211/1");

        config.setDefaultCacheExpireSeconds(30);

        final SimpleCache cache = new SimpleCache(config);
        cache.put("name", "HeYiding", true);
        System.out.println(cache.get("name"));
    }
}
