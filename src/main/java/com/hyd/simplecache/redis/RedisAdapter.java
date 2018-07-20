package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.utils.Str;
import org.nustaq.serialization.FSTConfiguration;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

/**
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisAdapter implements CacheAdapter {

    private static FSTConfiguration FST;

    static {
        FST = FSTConfiguration.createDefaultConfiguration();
        FST.setForceSerializable(true);
    }

    private RedisConfiguration configuration;

    private Jedis jedis;

    public RedisAdapter(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.jedis = new Jedis(configuration.getServer(), configuration.getPort());

        if (!Str.isEmpty(configuration.getPassword())) {
            this.jedis.auth(configuration.getPassword());
        }
    }

    public static Object deserialize(byte[] bytes) {
        return FST.asObject(bytes);
    }

    public static byte[] serialize(Object serializable) {
        return FST.asByteArray(serializable);
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    private <T> T withJedis(JedisExecutor<T> executor) {
        try {
            return executor.execute(jedis);
        } finally {
            jedis.close();
        }
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public Object get(final String key) {
        return withJedis(new JedisExecutor<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                if (configuration.getTimeToIdleSeconds() > 0) {
                    jedis.expire(key, configuration.getTimeToIdleSeconds());
                }

                byte[] bytes = jedis.get(key.getBytes());

                if (bytes == null) {
                    return null;
                } else {
                    return deserialize(bytes);
                }
            }
        });
    }

    @Override
    public void touch(final String key) {
        withJedis(new JedisExecutor<Void>() {

            @Override
            public Void execute(Jedis jedis) {
                if (configuration.getTimeToIdleSeconds() > 0) {
                    jedis.expire(key, configuration.getTimeToIdleSeconds());
                }
                return null;
            }
        });
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public void put(final String key, final Object value, final boolean forever) {
        withJedis(new JedisExecutor<Void>() {

            @Override
            public Void execute(Jedis jedis) {
                if (forever) {
                    jedis.set(key.getBytes(), serialize(value));
                } else {
                    int ttl = configuration.getTimeToLiveSeconds();
                    jedis.setex(key.getBytes(), ttl, serialize(value));
                }
                return null;
            }
        });
    }

    @Override
    public void put(final String key, final Object value, final int timeToLiveSeconds) {
        withJedis(new JedisExecutor<Void>() {

            @Override
            public Void execute(Jedis jedis) {
                jedis.setex(key.getBytes(), timeToLiveSeconds, serialize(value));
                return null;
            }
        });
    }

    @Override
    public void delete(final String key) {
        withJedis(new JedisExecutor<Void>() {

            @Override
            public Void execute(Jedis jedis) {
                jedis.del(key.getBytes());
                return null;
            }
        });
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear is not supported by redis.");
    }

    @Override
    public boolean compareAndSet(
            final String key, final Object findValue, final Object setValue
    ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("cas operation is not supported by redis.");
    }

    @Override
    public void dispose() {
        this.jedis.close();
    }

    @Override
    public Iterator<String> keys() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("iteration of keys is not supported by redis.");
    }

    @Override
    public boolean keyExists(final String key) {
        return withJedis(new JedisExecutor<Boolean>() {

            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(key.getBytes());
            }
        });
    }

    private interface JedisExecutor<T> {

        T execute(Jedis jedis);
    }
}
