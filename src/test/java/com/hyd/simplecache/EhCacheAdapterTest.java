package com.hyd.simplecache;

import org.junit.Assert;
import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class EhCacheAdapterTest {

    @Test
    public void testCas() throws Exception {
        SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
        cache.put("name", "simplecache");
        Assert.assertEquals("simplecache", cache.get("name"));
    }
}
