package com.hyd.simplecache;

import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class EhCacheAdapterTest {

    @Test
    public void testCas() throws Exception {
        SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
        cache.put("name", "simplecache");

/*
        assertTrue(cache.compareAndSet("name", "simplecache", "simplecache2"));
        assertEquals("simplecache2", cache.get("name"));

        assertFalse(cache.compareAndSet("name", "simplecache3", "simplecache"));
        assertEquals("simplecache2", cache.get("name"));
*/
    }
}
