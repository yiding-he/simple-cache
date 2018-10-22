package com.hyd.simplecache.redis;

import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import org.junit.Assert;
import org.junit.Test;

public class RedisSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        RedisConfiguration c = new RedisConfiguration("127.0.0.1", 6379);
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);

        User user = new User();
        user.setUsername("user1");
        user.setPassword("pass1");
        cache.put("user", user);

        User cachedUser = cache.get("user");
        Assert.assertEquals(user.getUsername(), cachedUser.getUsername());
        Assert.assertEquals(user.getPassword(), cachedUser.getPassword());
    }

    @Test
    public void testAuth() throws Exception {
        RedisConfiguration c = new RedisConfiguration("127.0.0.1", 6379, "pass123");
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        cache.put("name", "simple-cache");
        String name = cache.get("name");
        Assert.assertEquals("simple-cache", name);
    }

    @Test
    public void testAuthFail() throws Exception {
        RedisConfiguration c = new RedisConfiguration("127.0.0.1", 6379, "pass1234");
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        cache.put("name", "simple-cache");
        String name = cache.get("name");
        Assert.assertEquals("simple-cache", name);
    }
}
