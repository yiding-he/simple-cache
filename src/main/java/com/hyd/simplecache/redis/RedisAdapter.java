package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.RedisConfiguration;
import org.nustaq.serialization.FSTConfiguration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

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

    private final ShardedJedisPool shardedJedisPool;

    public RedisAdapter(RedisConfiguration configuration) {
        this.configuration = configuration;
        shardedJedisPool = new ShardedJedisPool(new JedisPoolConfig(), configuration.getShardInfoList());
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
        ShardedJedis jedis = shardedJedisPool.getResource();
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
            public Object execute(ShardedJedis jedis) {
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
            public Void execute(ShardedJedis jedis) {
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
            public Void execute(ShardedJedis jedis) {
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
            public Void execute(ShardedJedis jedis) {
                jedis.setex(key.getBytes(), timeToLiveSeconds, serialize(value));
                return null;
            }
        });
    }

    @Override
    public void delete(final String key) {
        withJedis(new JedisExecutor<Void>() {

            @Override
            public Void execute(ShardedJedis jedis) {
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
    public void dispose() {
        this.shardedJedisPool.close();
    }

    @Override
    public Iterator<String> keys() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("iteration of keys is not supported by redis.");
    }

    @Override
    public boolean keyExists(final String key) {
        return withJedis(new JedisExecutor<Boolean>() {

            @Override
            public Boolean execute(ShardedJedis jedis) {
                return jedis.exists(key.getBytes());
            }
        });
    }

    private interface JedisExecutor<T> {

        T execute(ShardedJedis jedis);
    }
}
