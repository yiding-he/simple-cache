package com.hyd.simplecache.redis;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import org.junit.Assert;
import org.junit.Test;

public class RedisSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        RedisConfiguration c = RedisConfiguration.singleServer("127.0.0.1", 6379);
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
}
