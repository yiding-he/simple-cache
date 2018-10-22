package com.hyd.simplecache.memcached;

import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import org.junit.Assert;
import org.junit.Test;

public class MemcachedSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {

        SimpleCache cache = new SimpleCache(new MemcachedConfiguration());

        User user = new User();
        user.setUsername("user1");
        user.setPassword("pass1");
        cache.put("user", user);

        User cachedUser = cache.get("user");
        Assert.assertEquals(user.getUsername(), cachedUser.getUsername());
        Assert.assertEquals(user.getPassword(), cachedUser.getPassword());
    }
}
