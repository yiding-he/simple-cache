package com.hyd.simplecache;

import com.google.code.yanf4j.config.Configuration;
import net.rubyeye.xmemcached.MemcachedClient;

import java.util.ArrayList;
import java.util.List;

public class MemcachedConfiguration implements CacheConfiguration {

    public static final String DEFAULT_NAMESPACE = "__default__";

    public static final String DEFAULT_ADDRESS = "localhost:11211";

    private String namespace;

    private Configuration configuration = new Configuration();

    private int connectionPoolSize;                                 // 连接池大小

    private long opTimeout = MemcachedClient.DEFAULT_OP_TIMEOUT;    // 操作超时

    private int defaultCacheExpireSeconds = Integer.MAX_VALUE;  // 保存时间

    private int timeToIdle = 0;                                 // 闲置超时时间（秒）

    private List<WeightedAddress> addresses = new ArrayList<WeightedAddress>();

    public MemcachedConfiguration() {
        this(DEFAULT_NAMESPACE, DEFAULT_ADDRESS);
    }

    public MemcachedConfiguration(String namespace, String addresses) {
        this.namespace = namespace;
        parseAddress(addresses);
    }

    public static List<WeightedAddress> psrseAddressList(String addresses) {
        String[] addrs = addresses.split(";|,");
        List<WeightedAddress> list = new ArrayList<WeightedAddress>();

        for (String addr : addrs) {
            addr = addr.trim();

            if (addr.length() == 0) {
                continue;
            }

            if (!addr.contains("/")) {      // 补充权重以方便后面解析
                addr = addr + "/1";
            }

            int splitIndex1 = addr.indexOf(":");
            int splitIndex2 = addr.indexOf("/");

            String host = addr.substring(0, splitIndex1);
            int port = Integer.parseInt(addr.substring(splitIndex1 + 1, splitIndex2));
            int weight = Integer.parseInt(addr.substring(splitIndex2 + 1));

            WeightedAddress a = new WeightedAddress(host, port, weight);
            list.add(a);
        }
        return list;
    }

    private void parseAddress(String addresses) {
        List<WeightedAddress> list = psrseAddressList(addresses);
        this.addresses.addAll(list);
    }

    public int getTimeToIdle() {
        return timeToIdle;
    }

    public void setTimeToIdle(int timeToIdle) {
        this.timeToIdle = timeToIdle;
    }

    public int getDefaultCacheExpireSeconds() {
        return defaultCacheExpireSeconds;
    }

    public void setDefaultCacheExpireSeconds(int defaultCacheExpireSeconds) {
        this.defaultCacheExpireSeconds = defaultCacheExpireSeconds;
    }

    public long getOpTimeout() {
        return opTimeout;
    }

    public void setOpTimeout(long opTimeout) {
        this.opTimeout = opTimeout;
    }

    public List<WeightedAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        parseAddress(addresses);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setCheckSessionTimeoutInterval(long value) {
        configuration.setCheckSessionTimeoutInterval(value);
    }

    public void setDispatchMessageThreadCount(int value) {
        configuration.setDispatchMessageThreadCount(value);
    }

    public void setHandleReadWriteConcurrently(boolean value) {
        configuration.setHandleReadWriteConcurrently(value);
    }

    public void setReadThreadCount(int value) {
        configuration.setReadThreadCount(value);
    }

    public void setSessionIdleTimeout(long value) {
        configuration.setSessionIdleTimeout(value);
    }

    public void setSessionReadBufferSize(int value) {
        configuration.setSessionReadBufferSize(value);
    }

    public void setSoTimeout(int value) {
        configuration.setSoTimeout(value);
    }

    public void setStatisticsInterval(long value) {
        configuration.setStatisticsInterval(value);
    }

    public void setStatisticsServer(boolean value) {
        configuration.setStatisticsServer(value);
    }

    public void setWriteThreadCount(int value) {
        configuration.setWriteThreadCount(value);
    }
}
