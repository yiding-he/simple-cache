package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.RedisConfiguration;
import com.hyd.simplecache.SimpleCacheException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.Iterator;

/**
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisAdapter implements CacheAdapter {

    private RedisConfiguration configuration;

    private JedisPool jedisPool;

    public RedisAdapter(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.jedisPool = new JedisPool(new JedisPoolConfig(),
                configuration.getServer(), configuration.getPort(), 1000, configuration.getPassword());
    }

    public static Serializable deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            return (Serializable) new ObjectInputStream(in).readObject();
        } catch (java.io.InvalidClassException e) {
            return null;
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    public static byte[] serialize(Serializable serializable) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(serializable);
            return out.toByteArray();
        } catch (IOException e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    private <T> T withJedis(JedisExecutor<T> executor) {
        Jedis jedis = this.jedisPool.getResource();
        try {
            return executor.execute(jedis);
        } finally {
            this.jedisPool.returnResource(jedis);
        }
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public Serializable get(final String key) {
        return withJedis(new JedisExecutor<Serializable>() {

            @Override
            public Serializable execute(Jedis jedis) {
                return deserialize(jedis.get(key.getBytes()));
            }
        });
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public void put(final String key, final Serializable value, final boolean forever) {
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
    public void put(final String key, final Serializable value, final int timeToLiveSeconds) {
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
            final String key, final Serializable findValue, final Serializable setValue
    ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("cas operation is not supported by redis.");
    }

    @Override
    public void dispose() {
        this.jedisPool.destroy();
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
