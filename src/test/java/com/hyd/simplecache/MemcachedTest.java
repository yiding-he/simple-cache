package com.hyd.simplecache;

import java.util.concurrent.TimeUnit;

/**
 * (description)
 * created at 2015/9/19
 *
 * @author Yiding
 */
public class MemcachedTest {

    public static void main(String[] args) throws Exception {
        MemcachedConfiguration c = new MemcachedConfiguration("demo", "192.168.1.180:11211");
        c.setTimeToIdle(5);
        c.setDefaultCacheExpireSeconds(5);
        SimpleCache simpleCache = new SimpleCache(c);

        simpleCache.put("name", "heyiding");
        System.out.println(simpleCache.get("name"));

        TimeUnit.SECONDS.sleep(4);
        System.out.println(simpleCache.get("name"));

        TimeUnit.SECONDS.sleep(4);
        System.out.println(simpleCache.get("name"));

        TimeUnit.SECONDS.sleep(6);
        System.out.println(simpleCache.get("name"));

        simpleCache.close();
    }
}
