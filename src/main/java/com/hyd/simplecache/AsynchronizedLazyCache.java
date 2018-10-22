package com.hyd.simplecache;

import com.hyd.simplecache.utils.JsonUtils;
import com.hyd.simplecache.utils.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用于异步刷新操作的缓存。异步刷新操作只要缓存中有值，就不会挂起任何线程。
 * 注意：异步刷新的缓存的保存时间为缓存允许的最长时间（包括永久保存）。
 *
 * @author 贺一丁
 */
@SuppressWarnings({"unchecked"})
public abstract class AsynchronizedLazyCache<T> extends LazyCache<T> {

    public static final int DEFAULT_POOL_SIZE = 10;

    private static final Logger log = LoggerFactory.getLogger(LazyCache.class);

    private static final Logger LOG = LoggerFactory.getLogger(AsynchronizedLazyCache.class);

    /**
     * 刷新缓存的间隔时间。一个缓存被创建或刷新后隔一段时间将需要再次刷新。
     */
    private long refreshInterval;

    /**
     * 构造方法
     *
     * @param simpleCache     实际缓存
     * @param refreshInterval 缓存过期时间，单位毫秒
     */
    public AsynchronizedLazyCache(SimpleCache simpleCache, long refreshInterval) {
        super(simpleCache);
        this.refreshInterval = refreshInterval;
    }

    /**
     * 修改执行刷新的线程池大小
     *
     * @param poolSize 线程池大小
     */
    public static void setRefreshExecutorPoolSize(int poolSize) {
        RefreshTaskExecutor.executor.setMaximumPoolSize(poolSize);
    }

    /**
     * 关闭刷新任务队列，程序关闭时使用
     */
    public static void shutdownExecutor() {
        RefreshTaskExecutor.executor.shutdown();
    }

    @Override
    public T get(Object... parameters) {
        String cacheKey = getCacheKey(JsonUtils.toJson(parameters));
        Element element;
        T value = null;

        synchronized (LockFactory.getLock(cacheKey)) {
            element = getFromCacheSafely(cacheKey);

            if (element != null && !expired(element)) {
                return (T) element.getValue();
            }

            try {
                // 第一次取数据可能花时间较长，之后就不会了。
                if (element == null) {
                    value = fetch(parameters);
                } else {
                    scheduleRefreshTask(cacheKey, parameters);
                    value = (T) element.getValue();
                }

                // 如果取不到数据，则放一个包含 null 的 Element 到缓存里，一段
                // 时间内就不会再反复查询这个不存在的数据了
                int retryInterval = getRetryInterval();
                if (value == null && retryInterval > 0) {
                    log.debug("Saving null for key '{}' for {} second(s).", cacheKey, retryInterval);
                    this.simpleCache.putElement(cacheKey, new Element(null), retryInterval);
                }

                // 这里总是会更新缓存的访问时间，在异步操作下这样可以欺骗访问
                // 相同key的下一个线程，避免其再创建一个refreshTask。
                this.simpleCache.put(cacheKey, value, true);
            } catch (Exception e) {
                LOG.error("Cache fetch failed", e);
            }
        }

        return value;
    }

    // 从缓存获取值，如果缓存取值失败则返回 null
    private Element getFromCacheSafely(String key) {
        try {
            return this.simpleCache.getElement(key);
        } catch (SimpleCacheException e) {
            LOG.error("", e);
            return null;
        }
    }

    /**
     * 添加异步刷新任务
     *
     * @param key        缓存 key
     * @param parameters 用于获取数据的参数
     */
    private void scheduleRefreshTask(String key, Object[] parameters) {
        RefreshTaskExecutor.addTask(key, this, parameters);
    }

    private boolean expired(Element element) {
        long now = System.currentTimeMillis();
        LOG.debug("检查超时时间：now={},lastUpdate={}", now, element.getLastUpdate());
        return now - element.getLastUpdate() > refreshInterval;
    }

    ///////////////////////////////////////////////////////////////

    /**
     * 执行异步缓存刷新任务的队列
     */
    private static class RefreshTaskExecutor {

        private static final List<String> queuedKeys = new ArrayList<String>();

        static ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, DEFAULT_POOL_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        public static void addRunningCacheKey(String cacheKey) {
            queuedKeys.add(cacheKey);
        }

        ///////////////////////////////////////////////////////////////

        public static void deleteRunningCacheKey(String cacheKey) {
            queuedKeys.remove(cacheKey);
        }

        /**
         * 添加一个刷新任务到队列
         *
         * @param key   任务的 key
         * @param cache 执行刷新的 cache 对象
         */
        public static void addTask(String key, AsynchronizedLazyCache cache, Object[] parameters) {

            RefreshTask task = new RefreshTask(key, cache, parameters);

            // 如果该 cacheKey 已经在队列中，则跳过
            synchronized (queuedKeys) {
                if (queuedKeys.contains(key)) {
                    return;
                }
            }

            // 如果已经有一个相同的任务在队列中或者正在运行，则不需要再添加了
            if (executor.getQueue().contains(task)) {
                return;
            }

            executor.submit(task);
        }
    }

    ///////////////////////////////////////////////////////////////

    /**
     * 执行刷新的任务
     */
    private static class RefreshTask implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(RefreshTask.class);

        /**
         * 刷新任务的 key
         */
        private String cacheKey;

        /**
         * 执行刷新的 cache 对象
         */
        private AsynchronizedLazyCache cache;

        /**
         * 获取数据需要的参数
         */
        private Object[] parameters;

        public RefreshTask(String cacheKey, AsynchronizedLazyCache cache, Object[] parameters) {
            this.cacheKey = cacheKey;
            this.cache = cache;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            RefreshTaskExecutor.addRunningCacheKey(this.cacheKey);

            try {
                Object value = cache.fetch(this.parameters);

                // 当实现 AsynchronizedLazyCache 时，只要令 fetch()
                // 方法返回 null，就可以保持缓存中的内容不变。
                if (value != null) {
                    cache.simpleCache.put(cacheKey, value, true);
                }
            } catch (Exception e) {
                LOG.error("", e);
            } finally {
                RefreshTaskExecutor.deleteRunningCacheKey(this.cacheKey);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RefreshTask task = (RefreshTask) o;
            return !(cacheKey != null ? !cacheKey.equals(task.cacheKey) : task.cacheKey != null);
        }

        @Override
        public int hashCode() {
            return cacheKey != null ? cacheKey.hashCode() : 0;
        }
    }
}
