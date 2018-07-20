package com.hyd.simplecache;

import redis.clients.jedis.JedisShardInfo;

import java.util.List;

/**
 * (description)
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisConfiguration implements CacheConfiguration {

    private List<JedisShardInfo> shardInfoList;

    private int timeToIdleSeconds;

    private int timeToLiveSeconds;

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
