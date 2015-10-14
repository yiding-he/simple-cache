package com.hyd.simplecache;

/**
 * (description)
 * created at 2015/9/19
 *
 * @author Yiding
 */
public class MemcachedTest {

    public static void main(String[] args) {
        SimpleCache simpleCache = new SimpleCache(new MemcachedConfiguration("demo", "192.168.1.180:11211"));
        simpleCache.put("name", "heyiding");
        System.out.println(simpleCache.get("name"));
        simpleCache.close();
    }
}
