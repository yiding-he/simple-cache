package com.hyd.simplecache;

import com.hyd.simplecache.bean.User;
import com.hyd.simplecache.utils.JsonUtils;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * [description]
 *
 * @author yiding.he
 */
public class LazyCacheTest {

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

    ///////////////////////////////////////////////////////////////

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

    private static class UserDAO {

        public User findUserByName(String username) {
            return new User(username, "123456");
        }
    }
}
