package com.hyd.simplecache;

import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class TestMemcached {

    @Test
    public void testGetSave() throws Exception {
        MemcachedConfiguration config = new MemcachedConfiguration(
                "woplus.dispatch",
                "192.168.39.235:12000/1;"
        );

        SimpleCache cache = new SimpleCache(config);
        System.out.println(cache.get("2465.3176.3176_3_2588.android.0"));
    }
}
