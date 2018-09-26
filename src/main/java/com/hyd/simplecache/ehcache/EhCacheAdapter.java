package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.EhCacheConfiguration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import java.util.Iterator;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class EhCacheAdapter implements CacheAdapter {

    private Cache cache;

    private EhCacheConfiguration configuration;

    /**
     * 构造方法
     *
     * @param configuration 配置
     */
    public EhCacheAdapter(EhCacheConfiguration configuration) {
        this.configuration = configuration;
        initCache();
    }

    private void initCache() {
        CacheManager cacheManager = CacheManager.getInstance();
        String cacheName = configuration.getName();

        // 如果 CacheManager 已经有了同名 Cache，则使用已有的 Cache
        if (cacheManager.cacheExists(cacheName)) {
            this.cache = cacheManager.getCache(cacheName);
        } else {
            this.cache = new Cache(configuration.getConfiguration());
            cacheManager.addCache(this.cache);
        }
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return this.configuration;
    }

    private void checkCacheStatus() {
        if (this.cache.getStatus() != Status.STATUS_ALIVE) {
            synchronized (this) {
                if (this.cache.getStatus() != Status.STATUS_ALIVE) {
                    initCache();
                }
            }
        }
    }

    @Override
    public void touch(String key) {
        checkCacheStatus();
        this.cache.get(key);    // 触发 timeToIdle
    }

    @Override
    public Object get(String key) {
        checkCacheStatus();
        Element element = this.cache.get(key);
        return element == null ? null : (Object) element.getObjectValue();
    }

    @Override
    public void put(String key, Object value, boolean forever) {
        checkCacheStatus();

        Element element = new Element(key, value);

        if (forever) {
            element.setEternal(true);
        }

        this.cache.put(element);
    }

    @Override
    public void put(String key, Object value, int timeToLiveSeconds) {
        checkCacheStatus();

        Element element = new Element(key, value);
        element.setTimeToLive(timeToLiveSeconds);

        this.cache.put(element);
    }

    @Override
    public void delete(String key) {
        checkCacheStatus();
        this.cache.remove(key);
    }

    @Override
    public void clear() {
        checkCacheStatus();
        this.cache.removeAll();
    }

    @Override
    public void dispose() {
        CacheManager.getInstance().removeCache(this.cache.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<String> keys() {
        checkCacheStatus();
        return this.cache.getKeys().iterator();
    }

    @Override
    public boolean keyExists(String key) {
        checkCacheStatus();
        return this.cache.isKeyInCache(key);
    }
}
