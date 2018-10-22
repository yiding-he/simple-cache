package com.hyd.simplecache.memcached;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.MemcachedConfiguration;
import com.hyd.simplecache.SimpleCacheException;
import com.hyd.simplecache.utils.FstUtils;
import com.spotify.folsom.BinaryMemcacheClient;
import com.spotify.folsom.ConnectFuture;
import com.spotify.folsom.MemcacheClientBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对 Memcached 缓存的包装
 *
 * @author 贺一丁
 */
public class MemcachedAdapter implements CacheAdapter {

    private static final Logger log = LoggerFactory.getLogger(MemcachedAdapter.class);

    private MemcachedConfiguration configuration;

    private BinaryMemcacheClient<byte[]> client;

    public MemcachedAdapter(MemcachedConfiguration configuration) throws SimpleCacheException {
        this.configuration = configuration;

        MemcacheClientBuilder<byte[]> builder = MemcacheClientBuilder.newByteArrayClient()
                .withAddress(configuration.getHost(), configuration.getPort())
                .withConnections(configuration.getConcurrency())
                .withRequestTimeoutMillis(configuration.getTimeoutMillis());

        if (StringUtils.isNotBlank(configuration.getUsername()) &&
                StringUtils.isNotBlank(configuration.getPassword())) {
            builder.withUsernamePassword(configuration.getUsername(), configuration.getPassword());
        }

        try {
            this.client = builder.connectBinary();
            ConnectFuture.connectFuture(client).toCompletableFuture().get();
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Object get(String key) throws SimpleCacheException {
        try {
            byte[] bytes = client.get(key).toCompletableFuture().get();
            if (bytes == null) {
                return null;
            }
            return FstUtils.deserialize(bytes);
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public void touch(String key) {
        try {
            this.client.touch(key, configuration.getTimeToIdleSeconds());
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public void put(String key, Object value, boolean forever) {
        int expireSeconds = forever ? Integer.MAX_VALUE : configuration.getTimeToLiveSeconds();
        put(key, value, expireSeconds);
    }

    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        putDirectly((key), value, timeToLiveSeconds);
    }

    @Override
    public void delete(String key) {
        try {
            this.client.delete((key));
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    /**
     * 放置对象到缓存中
     *
     * @param key           加上了命名空间的缓存key
     * @param value         要缓存的对象
     * @param expireSeconds 缓存超时秒数
     */
    private void putDirectly(String key, Object value, int expireSeconds) {
        try {
            byte[] bytes = FstUtils.serialize(value);
            this.client.set(key, bytes, expireSeconds);
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Clear operation on memcached is not supported.");
    }

    @Override
    public void dispose() {
        try {
            this.client.shutdown();
            this.client = null;
        } catch (Exception e) {
            log.warn("Failed to shutdown memcached client:", e);
        }
    }

    @Override
    public boolean keyExists(String key) {
        try {
            return this.client.get(key) != null;
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }
}
