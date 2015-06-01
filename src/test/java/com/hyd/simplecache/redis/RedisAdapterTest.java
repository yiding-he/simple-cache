package com.hyd.simplecache.redis;

import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.bean.User;
import org.junit.Test;

public class RedisAdapterTest {

    private RedisAdapter getAdapter() {
        RedisConfiguration c = new RedisConfiguration();
        c.setServer("127.0.0.1");
        c.setPort(6379);
        c.setTimeToLiveSeconds(60);

        return new RedisAdapter(c);
    }

    @Test
    public void testGetSet() throws Exception {
        RedisAdapter redisAdapter = getAdapter();
        System.out.println(redisAdapter.keyExists("name"));

        redisAdapter.put("name", "HeYiding", false);
        System.out.println(redisAdapter.get("name"));
    }

    @Test
    public void testSerialization() throws Exception {
        RedisAdapter redisAdapter = getAdapter();
/*
        User user = new User("admin", "pass");
        redisAdapter.put("user", user, true);
*/

        User _user = (User) redisAdapter.get("user");
        System.out.println(_user.getUsername());
        System.out.println(_user.getPassword());
    }
}