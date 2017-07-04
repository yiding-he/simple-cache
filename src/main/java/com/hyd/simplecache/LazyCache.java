package com.hyd.simplecache;

import com.hyd.simplecache.utils.JsonUtils;
import com.hyd.simplecache.utils.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 预先定义查询方法的缓存。使用时调用其 {@link #get(Object...)} 方法即可，LazyCache 会判断是否有缓存，有
 * 就直接返回缓存，否则调用 {@link #fetch(Object[])} 方法并将结果缓存起来，然后再返回。
 * <p>
 * 要使用 LazyCache，需要根据其存取的对象类型实现一个具名或匿名的子类。例如：
 * <p>
 * <pre>
 *     SimpleCache simpleCache = createSimpleCache();
 *
 *     LazyCache<String> usernameCache = new LazyCache(simpleCache) {
 *
 *         &#64;Override
 *         protected String fetch(Object... parameters) throws Exception {
 *             String userid = (Long)parameters[0];
 *             return getUserNameById(userid);
 *         }
 *     };
 *
 *     long userid = 10000;
 *     System.out.println("username of 10000: " + usernameCache.get(userid));
 * </pre>
 *
 * @author yiding
 */
@SuppressWarnings({"unchecked"})
public abstract class LazyCache<T extends Serializable> {

    private static final String SEPERATOR = "//";   // ESC

    private static final Logger log = LoggerFactory.getLogger(LazyCache.class);

    protected SimpleCache simpleCache;

    /**
     * 构造方法
     *
     * @param simpleCache 实际缓存
     */
    public LazyCache(SimpleCache simpleCache) {
        this.simpleCache = simpleCache;
    }

    /**
     * 获取数据。
     * 注意：这个地方没有定义为 varargs，是因为某些 Java 环境下可能会将 parameters 当作第一个参数来传。
     *
     * @param parameters 参数值
     *
     * @return 获取到的数据
     *
     * @throws Exception 如果获取数据失败
     */
    protected abstract T fetch(Object[] parameters) throws Exception;

    protected String getCacheKey(String key) {
        // Memcached 不允许 key 中带有空白字符和控制字符
        return (getPrefix() + SEPERATOR + key).replaceAll("\\s+", "");
    }

    /**
     * 通过继承的方式获取缓存值。如果缓存中没有，则调用子类的
     * {@link #fetch(Object[])} 方法获取数据缓存并返回。
     *
     * @param parameters 查询参数
     *
     * @return 缓存值
     */
    public T get(Object... parameters) {
        String cacheKey = getCacheKey(JsonUtils.toJson(parameters));
        T value = null;

        synchronized (LockFactory.getLock(cacheKey)) {
            Element<Serializable> element = this.simpleCache.getElement(cacheKey);

            if (element == null) {
                try {
                    value = this.fetch(parameters);

                    if (value != null) {
                        int cacheDuration = getCacheDuration();

                        if (cacheDuration == -1) {
                            this.simpleCache.put(cacheKey, value);
                        } else {
                            this.simpleCache.put(cacheKey, value, cacheDuration);
                        }
                    }
                } catch (Exception e) {
                    log.error("Cache fetching error for key '" + cacheKey + "'", e);
                }

                // 如果取不到数据，则放一个包含 null 的 Element 到缓存里，一段
                // 时间内就不会再反复查询这个不存在的数据了
                int retryInterval = getRetryInterval();
                if (value == null && retryInterval > 0) {
                    log.debug("Saving null for key '{}' for {} second(s).", cacheKey, retryInterval);
                    this.simpleCache.putElement(cacheKey, new Element(null), retryInterval);
                }
            } else {
                value = (T) element.getValue();
            }
        }

        return value;
    }

    /**
     * 获取缓存时间（秒）。当没有设置缓存时间（或返回 -1）时，以 {@link this#simpleCache} 的缓存时间为准。
     *
     * @return 缓存时间，缺省情况下返回 -1，表示没有设置。子类可以覆写本方法。
     */
    protected int getCacheDuration() {
        return -1;
    }

    /**
     * 获取失败重试间隔时间。如果对指定的 key 查询数据不存在或失败，则在指定时间
     * 内不再重复查询。这是为了避免当负载高时大量重复查询不存在的数据
     *
     * @return 失败重试间隔时间
     */
    protected int getRetryInterval() {
        return 0;
    }

    /**
     * 获取 key 前缀，供子类实现。多个 LazyCache 使用同一个
     * SimpleCache 时，为了避免 key 冲突，需要各自指定不同的前缀。
     *
     * @return key 前缀
     */
    protected String getPrefix() {
        return this.getClass().getName();
    }

    /**
     * 删除指定缓存
     *
     * @param parameters 获取缓存值的参数
     */
    public void delete(Object... parameters) {
        String cacheKey = getCacheKey(JsonUtils.toJson(parameters));
        this.simpleCache.delete(cacheKey);
    }
}
