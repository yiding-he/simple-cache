package com.hyd.simplecache.redis;

import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import com.hyd.simplecache.utils.JsonUtils;
import org.junit.Test;

/**
 * (description)
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        RedisConfiguration c = new RedisConfiguration();
        c.setServer("127.0.0.1");
        c.setPort(6379);
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        User user = cache.get("user");

        System.out.println(JsonUtils.toJson(user));
    }

    @Test
    public void testAuth() throws Exception {
        RedisConfiguration c = new RedisConfiguration();
        c.setServer("127.0.0.1");
        c.setPort(6379);
        c.setPassword("pass123");
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        cache.put("name", "simple-cache");
        System.out.println(cache.get("name"));
    }

    @Test
    public void testAuthFail() throws Exception {
        RedisConfiguration c = new RedisConfiguration();
        c.setServer("127.0.0.1");
        c.setPort(6379);
        c.setPassword("pass1234");   // wrong pass
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        cache.put("name", "simple-cache");
        System.out.println(cache.get("name"));
    }
}
