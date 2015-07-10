package com.hyd.simplecache;

import com.hyd.simplecache.bean.User;
import com.hyd.simplecache.utils.JsonUtils;
import org.junit.Test;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * [description]
 *
 * @author yiding.he
 */
public class LazyCacheTest {

    private static Random R = new SecureRandom();

    ///////////////////////////////////////////////////////////////

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String threadName() {
        return Thread.currentThread().getName();
    }

    @Test
    public void testLazyCache() throws Exception {

        final UserDAO userDAO = new UserDAO();
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setName("user-cache");
        SimpleCache simpleCache = new SimpleCache(configuration);

        LazyCache<User> userCache = new LazyCache<User>(simpleCache) {

            @Override
            protected User fetch(Object... parameters) throws Exception {
                String username = (String) parameters[0];
                return userDAO.findUserByName(username);
            }
        };

        User adminUser = userCache.get("admin");
        System.out.println(JsonUtils.toJson(adminUser));
    }

    @Test
    public void testNullValue() throws Exception {
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setName("user-cache");
        SimpleCache simpleCache = new SimpleCache(configuration);

        LazyCache lazyCache = new LazyCache(simpleCache) {

            @Override
            protected Serializable fetch(Object[] parameters) throws Exception {
                System.out.println("Fetching data...");
                return null;
            }
        };

        for (int i = 0; i < 20; i++) {
            TimeUnit.MILLISECONDS.sleep(200);
            System.out.println(lazyCache.get("key"));
        }
    }

    @Test
    public void testConcurrency() throws Exception {
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setName("user-cache");
        SimpleCache simpleCache = new SimpleCache(configuration);

        final LazyCache<String> lazyCache = new LazyCache<String>(simpleCache) {

            @Override
            protected String fetch(Object[] parameters) throws Exception {
                sleep(R.nextInt(1000) + 100);
                return "value" + R.nextInt(1000);
            }
        };

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println(threadName() + " started...");
                System.out.println(threadName() + " get value: " + lazyCache.get("1"));
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(runnable);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }

    private static class UserDAO {

        public User findUserByName(String username) {
            return new User(username, "123456");
        }
    }
}
