package com.hyd.simplecache;

import com.hyd.simplecache.ehcache.EhCacheConfiguration;
import org.junit.Test;

public class SimpleCacheTest {

    @Test
    public void testGetAsync() throws Exception {
        EhCacheConfiguration conf = new EhCacheConfiguration();
        SimpleCache simpleCache = new SimpleCache(conf);

        for (int i = 0; i < 30; i++) {
            Long value = simpleCache.getAsync("key", 3, System::currentTimeMillis);
            System.out.println(value);
            Thread.sleep(1000);
        }
    }
}
