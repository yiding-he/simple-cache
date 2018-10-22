package com.hyd.simplecache;

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
 */
@SuppressWarnings("unchecked")
public class SimpleCache {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCache.class);

    private CacheAdapter cacheAdapter;

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

    /**
     * 获取当前缓存的配置
     *
     * @return 当前缓存的配置
     */
    public CacheConfiguration getConfiguration() {
        return cacheAdapter.getConfiguration();
    }

    /**
     * 保存 Element 对象到缓存，使用缺省的缓存超时时间
     *
     * @param key     键
     * @param element 要缓存的元素
     */
    public void putElement(String key, Element element) {
        this.cacheAdapter.put(key, element, false);
    }

    /**
     * 保存 Element 对象到缓存，并指定超时时间或永久保存
     *
     * @param key               键
     * @param element           要缓存的元素
     * @param timeToLiveSeconds 缓存时间，0 或负数表示永久保存
     */
    public void putElement(String key, Element element, int timeToLiveSeconds) {
        if (timeToLiveSeconds > 0) {
            this.cacheAdapter.put(key, element, timeToLiveSeconds);
        } else {
            this.cacheAdapter.put(key, element, true);
        }
    }

    /**
     * 放置对象到缓存中
     *
     * @param key   对象key
     * @param value 对象值
     */
    public void put(String key, Object value) {
        Element element = new Element(value);
        this.cacheAdapter.put(key, element, false);
    }

    /**
     * 放置对象到缓存中
     *
     * @param key   对象key
     * @param value 对象值
     */
    public void put(String key, Object value, boolean forever) {
        Element element = new Element(value);
        this.cacheAdapter.put(key, element, forever);
    }

    /**
     * 放置对象到缓存中
     *
     * @param key        对象key
     * @param value      对象值
     * @param timeToLive 本条缓存的保存时长（秒）
     */
    public void put(String key, Object value, int timeToLive) {
        Element element = new Element(value);
        this.cacheAdapter.put(key, element, timeToLive);
    }

    /**
     * 更新缓存的超时时间，超时时间由属性 timeToIdle 决定。
     *
     * @param key 要更新超时时间的缓存 key
     */
    public void touch(String key) {
        this.cacheAdapter.touch(key);
    }

    /**
     * 从缓存中获取对象
     *
     * @param key 对象key
     *
     * @return 对象值
     */
    public <T> T get(String key) {
        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            LOG.info("Cache missing: {}", key);
            return null;
        }

        // 剥去包装
        if (value instanceof Element) {
            value = ((Element) value).getValue();
        } else {
            LOG.debug("Cache value of '" + key + "' is not an Element: " + value.getClass());
        }

        return (T) value;
    }

    public <T> T get(String key, Provider<T> provider) {
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

        return (T) value;
    }

    public <T> T get(String key, Provider<T> provider, int cacheTimeoutSeconds) {
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

        return (T) value;
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
        Object value = get(key);

        if (value == null) {
            return null;
        }

        if (!type.isAssignableFrom(value.getClass())) {
            throw new ClassCastException("Cache value of \"" + key + "\" of type "
                    + value.getClass() + " cannot be cast to " + type.getClass());
        }

        return (T) value;
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
    public <E> Element<E> getElement(String key) throws SimpleCacheException {
        Object value = this.cacheAdapter.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Element) {
            return (Element<E>) value;
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
        this.cacheAdapter.delete(key);
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
