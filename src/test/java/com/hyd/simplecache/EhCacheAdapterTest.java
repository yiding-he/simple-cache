package com.hyd.simplecache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class EhCacheAdapterTest {

    @Test
    public void testCas() throws Exception {
        SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
        cache.put("name", "talkweb");

        cache.compareAndSet("name", "talkweb", "talkweb2");
        assertEquals("talkweb2", cache.get("name"));

        cache.compareAndSet("name", "talkweb3", "talkweb");
        assertEquals("talkweb2", cache.get("name"));
    }
}
