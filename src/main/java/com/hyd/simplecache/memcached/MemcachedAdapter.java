package com.hyd.simplecache.memcached;

import com.hyd.simplecache.*;
import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 对 Memcached 缓存的包装
 *
 * @author 贺一丁
 */
public class MemcachedAdapter implements CacheAdapter {

    private static final Logger log = LoggerFactory.getLogger(MemcachedAdapter.class);

    private MemcachedConfiguration configuration;

    private MemcachedClient client;

    public MemcachedAdapter(MemcachedConfiguration configuration) throws SimpleCacheException {
        this.configuration = configuration;

        XMemcachedClientBuilder builder = createBuilder(configuration);
        builder.setConfiguration(configuration.getConfiguration());
        if (configuration.getConnectionPoolSize() > 0) {
            builder.setConnectionPoolSize(configuration.getConnectionPoolSize());
        }

        try {
            this.client = builder.build();
            this.client.setOpTimeout(configuration.getOpTimeout());
        } catch (IOException e) {
            throw new SimpleCacheException(e);
        }
    }

    // 根据配置创建 ClientBuilder
    private XMemcachedClientBuilder createBuilder(MemcachedConfiguration configuration) {
        List<WeightedAddress> addresses = configuration.getAddresses();

        List<InetSocketAddress> socketAddresses = new ArrayList<InetSocketAddress>();
        int[] weights = new int[addresses.size()];

        for (int i = 0; i < addresses.size(); i++) {
            WeightedAddress address = addresses.get(i);
            socketAddresses.add(new InetSocketAddress(address.getHost(), address.getPort()));
            weights[i] = address.getWeight();
        }

        return new XMemcachedClientBuilder(socketAddresses, weights);
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Serializable get(String key) throws SimpleCacheException {
        this.client.beginWithNamespace(configuration.getNamespace());

        try {
            if (configuration.getTimeToIdle() > 0) {
                this.client.touch(key, configuration.getTimeToIdle());
            }
            return this.client.get(key);
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        } finally {
            this.client.endWithNamespace();
        }
    }

    @Override
    public void touch(String key) {
        this.client.beginWithNamespace(configuration.getNamespace());

        try {
            if (configuration.getTimeToIdle() > 0) {
                this.client.touch(key, configuration.getTimeToIdle());
            }
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        } finally {
            this.client.endWithNamespace();
        }
    }

    @Override
    public void put(String key, Object value, boolean forever) {
        int expireSeconds = forever ? Integer.MAX_VALUE : configuration.getDefaultCacheExpireSeconds();
        put(key, value, expireSeconds);
    }

    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        putDirectly((key), value, timeToLiveSeconds);
    }

    @Override
    public void delete(String key) {
        this.client.beginWithNamespace(configuration.getNamespace());
        try {
            this.client.delete((key));
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        } finally {
            this.client.endWithNamespace();
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
        this.client.beginWithNamespace(configuration.getNamespace());
        try {
            this.client.set(key, expireSeconds, value);
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        } finally {
            this.client.endWithNamespace();
        }
    }

    @Override
    public void clear() {
        try {
            this.client.invalidateNamespace(configuration.getNamespace());
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        }
    }

    @Override
    public boolean compareAndSet(String key, Object findValue, final Object setValue) {
        this.client.beginWithNamespace(configuration.getNamespace());

        try {
            final GetsResponse<Object> gets = this.client.gets((key));

            if (!gets.getValue().equals(findValue)) {
                return false;
            }

            this.client.cas(key, new CASOperation<Object>() {

                @Override
                public int getMaxTries() {
                    return 1;
                }

                @Override
                public Object getNewValue(long currentCAS, Object currentValue) {
                    return setValue;
                }
            });

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            this.client.endWithNamespace();
        }
    }

    @Override
    public void dispose() {
        try {
            this.client.shutdown();
            this.client = null;
        } catch (IOException e) {
            log.warn("Failed to shutdown memcached client:", e);
        }
    }

    @Override
    public Iterator<String> keys() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Key iterator is not supported by memcached.");
    }

    @Override
    public boolean keyExists(String key) {
        this.client.beginWithNamespace(configuration.getNamespace());
        try {
            return this.client.get(key) != null;
        } catch (Exception e) {
            throw new SimpleCacheException(e);
        } finally {
            this.client.endWithNamespace();
        }
    }
}
