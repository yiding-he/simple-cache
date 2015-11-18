package com.hyd.simplecache.ehcache;

import com.hyd.simplecache.CacheAdapter;
import com.hyd.simplecache.CacheConfiguration;
import com.hyd.simplecache.EhCacheConfiguration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.Serializable;
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
        this.cache = new Cache(configuration.getConfiguration());

        CacheManager.getInstance().addCache(this.cache);
    }

    @Override
    public CacheConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void touch(String key) {
        this.cache.get(key);    // 触发 timeToIdle
    }

    @Override
    public Serializable get(String key) {
        Element element = this.cache.get(key);
        return element == null ? null : (Serializable) element.getObjectValue();
    }

    @Override
    public void put(String key, Serializable value, boolean forever) {
        Element element = new Element(key, value);

        if (forever) {
            element.setEternal(true);
        }

        this.cache.put(element);
    }

    @Override
    public void put(String key, Serializable value, int timeToLiveSeconds) {
        Element element = new Element(key, value);
        element.setTimeToLive(timeToLiveSeconds);

        this.cache.put(element);
    }

    @Override
    public void delete(String key) {
        this.cache.remove(key);
    }

    @Override
    public void clear() {
        this.cache.removeAll();
    }

    @Override
    public boolean compareAndSet(String key, Serializable findValue, Serializable setValue) {
        Element oldElement = new Element(key, findValue);
        Element newElement = new Element(key, setValue);
        return this.cache.replace(oldElement, newElement);
    }

    @Override
    public void dispose() {
        // Ehcache doesn't have this operation
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<String> keys() {
        return this.cache.getKeys().iterator();
    }

    @Override
    public boolean keyExists(String key) {
        return this.cache.isKeyInCache(key);
    }
}
