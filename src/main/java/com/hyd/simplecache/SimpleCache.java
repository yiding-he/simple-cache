package com.hyd.simplecache;

import com.hyd.simplecache.interceptor.CacheDeleteInterceptor;
import com.hyd.simplecache.interceptor.CacheGetInterceptor;
import com.hyd.simplecache.interceptor.CachePutInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>SimpleCache 的主要使用类。</p>
 * <p>这里对内容的存取都使用了 {@link Element} 类进行包装，存放的时候包装成 Element
 * 对象，取出的时候如果发现是 Element 对象，则取对象里面的值并返回。</p>
 * <p/>
 * 一个简单的例子：
 * <pre>
 *     SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
 *     cache.put("name", "张三");
 *     System.out.println(cache.get("name"));   // 输出 "张三"
 * </pre>
 *
 * @author 贺一丁
 */
@SuppressWarnings("unchecked")
public class SimpleCache {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCache.class);

    private CacheAdapter cacheAdapter;

    private CachePutInterceptor cachePutInterceptor;

    private CacheGetInterceptor cacheGetInterceptor;

    private CacheDeleteInterceptor deleteInterceptor;

    /**
     * 构造方法
     *
     * @param configuration 配置对象
     */
    public SimpleCache(CacheConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }

        cacheAdapter = CacheAdapterFactory.createCacheAdapter(configuration);
    }

    public CachePutInterceptor getCachePutInterceptor() {
        return cachePutInterceptor;
    }

    public void setCachePutInterceptor(CachePutInterceptor cachePutInterceptor) {
        this.cachePutInterceptor = cachePutInterceptor;
    }

    public CacheGetInterceptor getCacheGetInterceptor() {
        return cacheGetInterceptor;
    }

    public void setCacheGetInterceptor(CacheGetInterceptor cacheGetInterceptor) {
        this.cacheGetInterceptor = cacheGetInterceptor;
    }

    public CacheDeleteInterceptor getDeleteInterceptor() {
        return deleteInterceptor;
    }

    public void setDeleteInterceptor(CacheDeleteInterceptor deleteInterceptor) {
        this.deleteInterceptor = deleteInterceptor;
    }

    /**
     * 获取当前缓存的配置
     *
     * @return 当前缓存的配置
     */
    public CacheConfiguration getConfiguration() {
        return cacheAdapter.getConfiguration();
    }

    ////////////////////////////////////////////////////////////

    // 当从缓存中取到的对象类型不正确时，是否自动删除该缓存并填入正确的值
    private boolean refillOnTypeError = true;

    public boolean isRefillOnTypeError() {
        return refillOnTypeError;
    }

    public void setRefillOnTypeError(boolean refillOnTypeError) {
        this.refillOnTypeError = refillOnTypeError;
    }

    ////////////////////////////////////////////////////////////

    private boolean canPut(String key, Element element) {
        return this.cachePutInterceptor == null || this.cachePutInterceptor.cachePut(key, element.getValue());
    }

    private void putElementDefault(String key, Element element) {
        if (canPut(key, element)) {
            this.cacheAdapter.put(key, element, false);
        }
    }

    private void putElementWithTtl(String key, Element element, int ttl) {
        if (canPut(key, element)) {
            this.cacheAdapter.put(key, element, ttl);
        }
    }

    private void putElementForever(String key, Element element) {
        if (canPut(key, element)) {
            this.cacheAdapter.put(key, element, true);
        }
    }

    ////////////////////////////////////////////////////////////

    /**
     * 保存 Element 对象到缓存，并指定超时时间或永久保存
     *
     * @param key               键
     * @param element           要缓存的元素
     * @param timeToLiveSeconds 缓存时间，0 或负数表示永久保存
     */
    void putElement(String key, Element element, int timeToLiveSeconds) {
        if (timeToLiveSeconds > 0) {
            this.putElementWithTtl(key, element, timeToLiveSeconds);
        } else {
            this.putElementForever(key, element);
        }
    }

    /**
     * 放置对象到缓存中
     *
     * @param key   对象key
     * @param value 对象值
     */
    public void put(String key, Object value) {
        this.put(key, value, false);
    }

    /**
     * 放置对象到缓存中
     *
     * @param key   对象key
     * @param value 对象值
     */
    public void put(String key, Object value, boolean forever) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value is null");
        }

        Element element = new Element(value);
        if (forever) {
            this.putElementForever(key, element);
        } else {
            this.putElementDefault(key, element);
        }
    }

    /**
     * 放置对象到缓存中
     *
     * @param key        对象key
     * @param value      对象值
     * @param timeToLive 本条缓存的保存时长（秒）
     */
    public void put(String key, Object value, int timeToLive) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value is null");
        }

        Element element = new Element(value);
        this.putElementWithTtl(key, element, timeToLive);
    }

    /**
     * 更新缓存的超时时间，超时时间由属性 timeToIdle 决定。
     *
     * @param key 要更新超时时间的缓存 key
     */
    public void touch(String key) {
        this.cacheAdapter.touch(key);
    }

    private <T> T getOrNothing(String key, T value) {
        return (this.cacheGetInterceptor == null || this.cacheGetInterceptor.cacheGet(key, value)) ? value : null;
    }

    private <T> Element<T> getOrNothing(String key, Element<T> element) {
        T value = element.getValue();
        return (this.cacheGetInterceptor == null || this.cacheGetInterceptor.cacheGet(key, value)) ? element : null;
    }

    private <T> T getOrNothingTypeSafe(String key, Provider<T> provider, Object value) {
        try {
            return getOrNothing(key, (T) value);
        } catch (ClassCastException e) {
            if (refillOnTypeError) {
                value = provider.provide();
                if (value != null) {
                    put(key, value);
                }
                return getOrNothing(key, (T) value);
            } else {
                throw e;
            }
        }
    }

    /**
     * 从缓存中获取对象
     *
     * @param key 对象key
     *
     * @return 对象值
     */
    public <T> T get(String key) {

        if (key == null) {
            return null;
        }

        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            return null;
        }

        // 剥去包装
        if (value instanceof Element) {
            value = ((Element) value).getValue();
        }

        return getOrNothing(key, (T) value);
    }

    public <T> T get(String key, Provider<T> provider) {

        if (key == null) {
            return null;
        }

        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            value = provider.provide();
            if (value != null) {
                put(key, value);
            }
        }

        // 剥去包装
        if (value instanceof Element) {
            value = ((Element) value).getValue();
        }

        return getOrNothingTypeSafe(key, provider, value);
    }

    public <T> T get(String key, Provider<T> provider, int cacheTimeoutSeconds) {

        if (key == null) {
            return null;
        }

        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            value = provider.provide();
            if (value != null) {
                if (cacheTimeoutSeconds < 0) {
                    put(key, value, true);
                } else if (cacheTimeoutSeconds > 0) {
                    put(key, value, cacheTimeoutSeconds);
                }
            }
        }

        // 剥去包装
        if (value instanceof Element) {
            value = ((Element) value).getValue();
        }

        return getOrNothingTypeSafe(key, provider, value);
    }

    /**
     * 从缓存中获取对象
     *
     * @param key  对象key
     * @param type 对象类型
     * @param <T>  对象类型
     *
     * @return 对象值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {

        if (key == null) {
            return null;
        }

        T value = get(key);

        if (value == null) {
            return null;
        }

        if (!type.isAssignableFrom(value.getClass())) {
            throw new ClassCastException("Cache value of \"" + key + "\" of type "
                    + value.getClass() + " cannot be cast to " + type.getClass());
        }

        return value;
    }

    /**
     * 从缓存中获取 Element 元素
     *
     * @param key 对象key
     *
     * @return 对象值
     *
     * @throws SimpleCacheException 如果缓存中的元素类型不是 Element
     */
    <E> Element<E> getElement(String key) throws SimpleCacheException {
        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Element) {
            Element<E> element = (Element<E>) value;
            return getOrNothing(key, element);
        } else {
            throw new SimpleCacheException(
                    "Cache value of \"" + key + "\" is not an Element (" + value.getClass() + ")");
        }
    }

    /**
     * 删除指定的 key
     *
     * @param key 要删除的 key
     */
    public void delete(String key) {

        if (key == null) {
            return;
        }

        if (deleteInterceptor == null || deleteInterceptor.cacheDelete(key)) {
            this.cacheAdapter.delete(key);
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        this.cacheAdapter.clear();
    }

    public void close() {
        this.cacheAdapter.dispose();
    }
}
