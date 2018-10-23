package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.SimpleCache;
import org.junit.Test;

import static org.junit.Assert.*;

public class EhCacheSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        SimpleCache simpleCache = new SimpleCache(new EhCacheConfiguration());
        String key = "key";
        String value = "value";

        simpleCache.put(key, value);
        assertNotNull(simpleCache.get(key));
        assertEquals(value, simpleCache.get(key));

        String invalidKey = "INVALID_KEY_" + System.currentTimeMillis();
        assertNull(simpleCache.get(invalidKey));
    }

    @Test
    public void testExpiry() throws Exception {
        EhCacheConfiguration config = new EhCacheConfiguration();
        config.setTimeToLiveSeconds(1);

        SimpleCache simpleCache = new SimpleCache(config);
        String key = "key";
        String value = "value";

        simpleCache.put(key, value);
        assertNotNull(simpleCache.get(key));

        Thread.sleep(1100);
        assertNull(simpleCache.get(key));
    }

    @Test
    public void testMultipleSimpleCache() throws Exception {
        SimpleCache simpleCache1 = new SimpleCache(new EhCacheConfiguration());
        SimpleCache simpleCache2 = new SimpleCache(new EhCacheConfiguration());
        SimpleCache simpleCache3 = new SimpleCache(new EhCacheConfiguration());

        String key = "key";
        String value = "value";

        simpleCache1.put(key, value);
        assertNotNull(simpleCache1.get(key));
        assertEquals(value, simpleCache1.get(key));
        assertNull(simpleCache2.get(key));
        assertNull(simpleCache3.get(key));
    }
}
