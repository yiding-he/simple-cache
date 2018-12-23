package com.hyd.simplecache.redis;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import com.hyd.simplecache.serialization.PredefinedSerializeMethod;
import org.junit.Test;

public class RedisMultipleSerializeMethodTest {

    @Test
    public void testMultipleSerializeMethod() throws Exception {

        RedisConfiguration c = new RedisConfiguration();
        SimpleCache cache = new SimpleCache(c);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass1");
        cache.put("user1", user1);

        c.setSerializeMethod(PredefinedSerializeMethod.JSON.getTag());

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass2");
        cache.put("user2", user2);

        System.out.println(cache.get("user1", User.class));
        System.out.println(cache.get("user1", User.class).getClass());
        System.out.println(cache.get("user2", User.class));
        System.out.println(cache.get("user2", User.class).getClass());
    }
}
