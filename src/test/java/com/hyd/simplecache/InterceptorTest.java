package com.hyd.simplecache;

import com.hyd.simplecache.interceptor.CacheDeleteInterceptor;
import com.hyd.simplecache.interceptor.CacheGetInterceptor;
import com.hyd.simplecache.interceptor.CachePutInterceptor;
import org.junit.Assert;
import org.junit.Test;

public class InterceptorTest {

    @Test
    public void testInterceptor() throws Exception {
        SimpleCache cache = new SimpleCache(new EhCacheConfiguration());

        cache.setCachePutInterceptor(new CachePutInterceptor() {

            @Override
            public boolean cachePut(String key, Object value) {
                return key.equals("can_put");
            }
        });

        cache.put("cannot_put", "123");
        Assert.assertNull(cache.get("cannot_put"));

        cache.put("can_put", "112233");
        Assert.assertEquals("112233", cache.get("can_put"));

        ////////////////////////////////////////////////////////////

        cache.setCacheGetInterceptor(new CacheGetInterceptor() {

            @Override
            public boolean cacheGet(String key, Object value) {
                return false;
            }
        });

        Assert.assertNull(cache.get("can_put"));
        cache.setCacheGetInterceptor(null);

        ////////////////////////////////////////////////////////////

        Assert.assertEquals("112233", cache.get("can_put"));

        cache.setDeleteInterceptor(new CacheDeleteInterceptor() {

            @Override
            public boolean cacheDelete(String key) {
                return false;
            }
        });

        cache.delete("can_put");
        Assert.assertEquals("112233", cache.get("can_put"));

        cache.setDeleteInterceptor(null);
        cache.delete("can_put");
        Assert.assertNull(cache.get("can_put"));
    }
}
