package com.hyd.simplecache;

import com.hyd.simplecache.utils.JsonUtils;
import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class AsynchronizedLazyCacheTest {

    @Test
    public void testGet() throws Exception {

        EhCacheConfiguration config = new EhCacheConfiguration();
        SimpleCache simpleCache = new SimpleCache(config);

        final AsynchronizedLazyCache<String> cache = new AsynchronizedLazyCache<String>(simpleCache, 3000) {

            boolean fail = false;

            @Override
            protected String fetch(Object... parameters) throws Exception {
                String key = JsonUtils.toJson(parameters);

                if (fail) {
                    fail = false;
                    throw new RuntimeException("haha");
                } else {
                    fail = true;
                }

                Thread.sleep(5000);  // 假设查询数据的时间很长
                return "value_" + key + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
            }

            @Override
            protected String getPrefix() {
                return "value:";
            }
        };

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    for (int i = 0; i < 20; i++) {
                        Thread.sleep(1000);
                        System.out.print("getting...");
                        String value = cache.get("haha!");
                        System.out.println(value);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(28000);
    }

    @Test
    public void testVarArgs() throws Exception {

        SimpleCache simpleCache = new SimpleCache(new EhCacheConfiguration());
        long cacheTimeout = 3000;           // 缓存超时时间为3秒

        AsynchronizedLazyCache<String> cache =
                new AsynchronizedLazyCache<String>(simpleCache, cacheTimeout) {

                    @Override
                    protected String fetch(Object... parameters) throws Exception {
                        String name = (String) parameters[0];
                        Thread.sleep(1000);         // 刷新缓存数据需要1秒
                        return name + ":" + System.currentTimeMillis();
                    }
                };


        for (int i = 0; i < 20; i++) {
            // 虽然每次取数据都要1秒，但cache.get()方法立刻就返回了
            String cacheValue = cache.get("cachekey");
            System.out.println(cacheValue);
            Thread.sleep(500);
        }
    }
}
