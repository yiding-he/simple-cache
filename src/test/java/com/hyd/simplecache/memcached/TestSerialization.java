package com.hyd.simplecache.memcached;

import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.NonSerializableUser;
import org.junit.Assert;
import org.junit.Test;

public class TestSerialization {

    @Test
    public void testPutObject() throws Exception {
        SimpleCache simpleCache = new SimpleCache(new MemcachedConfiguration("xxx", "localhost:11211/1"));
        simpleCache.put("user", new NonSerializableUser("admin", "admin"));
        NonSerializableUser user = simpleCache.get("user");
        Assert.assertNotNull(user);
    }
}
