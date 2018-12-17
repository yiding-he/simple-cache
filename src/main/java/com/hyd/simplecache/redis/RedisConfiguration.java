package com.hyd.simplecache.redis;

import com.hyd.simplecache.CacheAdapterFactory;
import com.hyd.simplecache.CacheConfiguration;

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

    public static RedisConfiguration singleServer(String host, int port) {
        RedisConfiguration c = new RedisConfiguration();
        c.setServers(Collections.singletonList(new RedisAddress(host, port, null)));
        return c;
    }

    ////////////////////////////////////////////////////////////

    private List<RedisAddress> servers;

    private int timeToIdleSeconds = 3600;

    private int timeToLiveSeconds = 3600;

    public void setServers(List<RedisAddress> servers) {
        this.servers = servers;
    }

    public List<RedisAddress> getServers() {
        return servers;
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
