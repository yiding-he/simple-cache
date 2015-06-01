package com.hyd.simplecache;

/**
 * (description)
 * created at 2015/3/16
 *
 * @author Yiding
 */
public class RedisConfiguration implements CacheConfiguration {

    private String server;

    private int port;

    private String password;

    private int timeToLiveSeconds;

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
