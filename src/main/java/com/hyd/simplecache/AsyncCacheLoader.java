package com.hyd.simplecache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 后台加载缓存内容
 */
public class AsyncCacheLoader {

    private static final int MAX_WORKERS = Math.min(5, Runtime.getRuntime().availableProcessors());

    private static final List<String> QUEUED_KEYS = new ArrayList<>();

    private static final ThreadPoolExecutor POOL_EXECUTOR =
            new ThreadPoolExecutor(1, MAX_WORKERS, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(POOL_EXECUTOR::shutdownNow));
    }

    /**
     * 添加一个刷新任务到队列
     */
    public static void addTask(String key, CacheAdapter cache, Supplier<?> supplier, long expiry) {

        RefreshTask task = new RefreshTask(key, cache, supplier, expiry);

        // 如果该 cacheKey 已经在队列中，则跳过
        synchronized (QUEUED_KEYS) {
            if (QUEUED_KEYS.contains(key)) {
                return;
            }
        }

        // 如果已经有一个相同的任务在队列中或者正在运行，则不需要再添加了
        if (POOL_EXECUTOR.getQueue().contains(task)) {
            return;
        }

        POOL_EXECUTOR.submit(task);
    }

    ////////////////////////////////////////////////////////////

    /**
     * 执行刷新的任务
     */
    private static class RefreshTask implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(RefreshTask.class);

        /**
         * 缓存 key
         */
        private String cacheKey;

        /**
         * 要保存到的缓存
         */
        private CacheAdapter cache;

        /**
         * 查询数据的方法
         */
        private Supplier<?> supplier;

        /**
         * 缓存超时时间（ms）
         */
        private long expiry;

        RefreshTask(String cacheKey, CacheAdapter cache, Supplier<?> supplier, long expiry) {
            this.cacheKey = cacheKey;
            this.cache = cache;
            this.supplier = supplier;
            this.expiry = expiry;
        }

        @Override
        public void run() {
            QUEUED_KEYS.add(cacheKey);

            try {
                Object value = supplier.get();
                if (value != null) {
                    Element<Object> element = new Element<>(value);
                    element.setExpiry(expiry);
                    cache.put(cacheKey, element, true);
                }
            } catch (Exception e) {
                LOG.error("", e);
            } finally {
                QUEUED_KEYS.remove(cacheKey);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RefreshTask task = (RefreshTask) o;
            return Objects.equals(cacheKey, task.cacheKey);
        }

        @Override
        public int hashCode() {
            return cacheKey != null ? cacheKey.hashCode() : 0;
        }
    }

}
