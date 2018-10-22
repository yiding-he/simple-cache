package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapterFactory;
import com.hyd.simplecache.CacheConfiguration;
import redis.clients.jedis.JedisShardInfo;

import java.util.Collections;
import java.util.List;

/**
 * Configurations for redis caching.
 *
 * @author Yiding
 */
public class RedisConfiguration implements CacheConfiguration {

    static {
        CacheAdapterFactory.register(RedisConfiguration.class, conf -> new RedisAdapter((RedisConfiguration) conf));
    }

    private List<JedisShardInfo> shardInfoList;

    private int timeToIdleSeconds = 3600;

    private int timeToLiveSeconds = 3600;

    private static JedisShardInfo createJedisShardInfo(String host, int port) {
        return new JedisShardInfo(host, port);
    }

    private static JedisShardInfo createJedisShardInfo(String host, int port, String pass) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
        jedisShardInfo.setPassword(pass);
        return jedisShardInfo;
    }

    public RedisConfiguration() {
    }

    public RedisConfiguration(String host, int port) {
        this(Collections.singletonList(
                createJedisShardInfo(host, port)
        ));
    }

    public RedisConfiguration(String host, int port, String pass) {
        this(Collections.singletonList(
                createJedisShardInfo(host, port, pass)
        ));
    }

    public RedisConfiguration(List<JedisShardInfo> shardInfoList) {
        this.shardInfoList = shardInfoList;
    }

    public List<JedisShardInfo> getShardInfoList() {
        return shardInfoList;
    }

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
