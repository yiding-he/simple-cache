package com.hyd.simplecache.redis;

import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import com.hyd.simplecache.utils.JsonUtils;
import org.junit.Test;
import redis.clients.jedis.JedisShardInfo;

import java.util.Collections;

/**
 * (description)
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisSimpleCacheTest {

    @Test
    public void testGetSet() throws Exception {
        RedisConfiguration c = new RedisConfiguration(Collections.singletonList(
                new JedisShardInfo("127.0.0.1", 6379)
        ));
        c.setTimeToLiveSeconds(60);

        SimpleCache cache = new SimpleCache(c);
        cache.delete("user");

        User user = cache.get("user");
        System.out.println("user = " + JsonUtils.toJson(user));  // should be null

        cache.put("user", new User("user1", "pass1"));
        user = cache.get("user");
        System.out.println("user = " + JsonUtils.toJson(user));
    }

}
