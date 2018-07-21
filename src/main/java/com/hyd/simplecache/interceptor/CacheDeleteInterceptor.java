package com.hyd.simplecache.interceptor;

public interface CacheDeleteInterceptor {

    /**
     * 拦截 delete 操作
     *
     * @param key 键
     *
     * @return 是否允许删除
     */
    boolean cacheDelete(String key);
}
