package com.hyd.simplecache;

public class MemcachedbCacheExpireTest {

    public static void main(String[] args) throws InterruptedException {
        MemcachedConfiguration config = new MemcachedConfiguration("", "192.168.39.243:10000");
        config.setDefaultCacheExpireSeconds(5);

        SimpleCache cache = new SimpleCache(config);
        cache.put("name", "value");

        System.out.println("name = " + cache.get("name"));      // 输出 value
        System.out.println("Waiting 10 seconds...");

        Thread.sleep(10000);

        System.out.println("name = " + cache.get("name"));      // 输出 null
    }
}
