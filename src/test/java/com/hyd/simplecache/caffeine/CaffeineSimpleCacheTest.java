package com.hyd.simplecache.caffeine;

import com.hyd.simplecache.CaffeineConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CaffeineSimpleCacheTest {

    @Test
    public void testGetSet() {
        SimpleCache simpleCache = new SimpleCache(new CaffeineConfiguration());
        String key = "key";
        String value = "value";

        simpleCache.put(key, value);
        assertEquals(value, simpleCache.get(key));

        simpleCache.delete(key);
        assertNull(simpleCache.get(key));
    }
}
