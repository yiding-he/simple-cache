package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.utils.FstUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisAdapter implements CacheAdapter {

    private RedisConfiguration configuration;

    private final ShardedJedisPool shardedJedisPool;

    public RedisAdapter(RedisConfiguration configuration) {
        this.configuration = configuration;
        shardedJedisPool = new ShardedJedisPool(new JedisPoolConfig(), createShardInfoList(configuration));
    }

    private List<JedisShardInfo> createShardInfoList(RedisConfiguration c) {
        return c.getServers().stream()
                .map(addr -> {
                    JedisShardInfo shardInfo = new JedisShardInfo(addr.getHost(), addr.getPort());
                    shardInfo.setPassword(addr.getPassword());
                    return shardInfo;
                })
                .collect(Collectors.toList());
    }

    private static Object deserialize(byte[] bytes) {
        return FstUtils.deserialize(bytes);
    }

    private static byte[] serialize(Object o) {
        return FstUtils.serialize(o);
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    private <T> T withJedis(JedisExecutor<T> executor) {
        try (ShardedJedis jedis = shardedJedisPool.getResource()) {
            return executor.execute(jedis);
        }
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public Object get(final String key) {
        return withJedis(jedis -> {
            if (configuration.getTimeToIdleSeconds() > 0) {
                jedis.expire(key, configuration.getTimeToIdleSeconds());
            }

            byte[] bytes = jedis.get(key.getBytes());

            if (bytes == null) {
                return null;
            } else {
                return deserialize(bytes);
            }
        });
    }

    @Override
    public void touch(final String key) {
        withJedis((JedisExecutor<Void>) jedis -> {
            if (configuration.getTimeToIdleSeconds() > 0) {
                jedis.expire(key, configuration.getTimeToIdleSeconds());
            }
            return null;
        });
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public void put(final String key, final Object value, final boolean forever) {
        withJedis((JedisExecutor<Void>) jedis -> {
            if (forever) {
                jedis.set(key.getBytes(), serialize(value));
            } else {
                int ttl = configuration.getTimeToLiveSeconds();
                jedis.setex(key.getBytes(), ttl, serialize(value));
            }
            return null;
        });
    }

    @Override
    public void put(final String key, final Object value, final int timeToLiveSeconds) {
        withJedis((JedisExecutor<Void>) jedis -> {
            jedis.setex(key.getBytes(), timeToLiveSeconds, serialize(value));
            return null;
        });
    }

    @Override
    public void delete(final String key) {
        withJedis((JedisExecutor<Void>) jedis -> {
            jedis.del(key.getBytes());
            return null;
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
    public boolean keyExists(final String key) {
        return withJedis(jedis -> jedis.exists(key.getBytes()));
    }

    private interface JedisExecutor<T> {

        T execute(ShardedJedis jedis);
    }
}
