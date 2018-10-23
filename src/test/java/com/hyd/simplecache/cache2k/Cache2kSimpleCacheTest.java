package com.hyd.simplecache.cache2k;

import com.hyd.simplecache.SimpleCache;
import org.junit.Test;

import static org.junit.Assert.*;

public class Cache2kSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        SimpleCache simpleCache = new SimpleCache(new Cache2kConfiguration());
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
        Cache2kConfiguration config = new Cache2kConfiguration();
        config.setExpireAfterWriteMillis(1000);

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
        SimpleCache simpleCache1 = new SimpleCache(new Cache2kConfiguration());
        SimpleCache simpleCache2 = new SimpleCache(new Cache2kConfiguration());
        SimpleCache simpleCache3 = new SimpleCache(new Cache2kConfiguration());

        String key = "key";
        String value = "value";

        simpleCache1.put(key, value);
        assertNotNull(simpleCache1.get(key));
        assertEquals(value, simpleCache1.get(key));
        assertNull(simpleCache2.get(key));
        assertNull(simpleCache3.get(key));

    }
}
