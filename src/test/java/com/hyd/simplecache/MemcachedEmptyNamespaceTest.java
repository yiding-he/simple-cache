package com.hyd.simplecache;

/**
 * @author yiding.he
 */
public class MemcachedEmptyNamespaceTest {

    public static void main(String[] args) {
        MemcachedConfiguration configuration = new MemcachedConfiguration("", "192.168.39.243:10000");
        SimpleCache cache = new SimpleCache(configuration);

        System.out.println(cache.get("user2_key"));

        cache.close();
    }
}
