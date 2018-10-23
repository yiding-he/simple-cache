package com.hyd.simplecache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Assert;
import org.junit.Test;

public class CaffeineTest {

    @Test
    public void testGetSet() throws Exception {
        Cache<Object, Object> cache = Caffeine.newBuilder().build();
        cache.put("name", "value");
        Assert.assertEquals("value", cache.getIfPresent("name"));
    }
}
