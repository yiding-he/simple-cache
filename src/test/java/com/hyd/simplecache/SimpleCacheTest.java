package com.hyd.simplecache;

import com.hyd.simplecache.ehcache.EhCacheFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class SimpleCacheTest {

    @Test
    public void testSimple() throws Exception {
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setTimeToLiveSeconds(30);

        SimpleCache cache = new SimpleCache(configuration);

        cache.put("name", "talkweb");
        assertEquals("talkweb", cache.get("name"));
    }

    @Test
    public void testSaveGet() throws Exception {
        SimpleCache cache = EhCacheFactory.getOrCreateCache("not-default");

        cache.put("name", "HeYiding");
        System.out.println(cache.get("name"));

        cache.clear();
        System.out.println(cache.get("name"));
    }

    @Test
    public void testRemoteCache() throws Exception {
        SimpleCache simpleCache = new SimpleCache(new MemcachedConfiguration("111", "192.168.39.243:10000"));
        simpleCache.put("name", "贺一丁", 10);

        Thread.sleep(12000);

        System.out.println(simpleCache.get("name"));
    }
}
