package com.hyd.simplecache.cache2k;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.junit.Assert;
import org.junit.Test;

public class Cache2kTest {

    @Test
    public void testGetSet() throws Exception {
        Cache<Object, Object> cache = Cache2kBuilder.forUnknownTypes().build();
        cache.put("name", "value");
        Assert.assertEquals("value", cache.get("name"));
    }
}
