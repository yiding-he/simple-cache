package com.hyd.simplecache.memcached;

import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.bean.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemcachedSimpleCacheTest {

    private SimpleCache cache = new SimpleCache(new MemcachedConfiguration());

    @Test
    public void testGetSet() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("pass1");
        cache.put("user", user);

        User cachedUser = cache.get("user");
        assertEquals(user.getUsername(), cachedUser.getUsername());
        assertEquals(user.getPassword(), cachedUser.getPassword());
    }

    @Test
    public void testExpire() throws Exception {
        cache.put("1_sec_key", "1_sec_value", 1);
        assertNotNull(cache.get("1_sec_key"));

        Thread.sleep(1500);
        assertNull(cache.get("1_sec_key"));
    }
}
