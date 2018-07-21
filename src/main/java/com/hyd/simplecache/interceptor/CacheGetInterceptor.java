package com.hyd.simplecache.interceptor;

public interface CacheGetInterceptor {

    /**
     * 拦截 get 操作
     *
     * @param key   键
     * @param value 值
     *
     * @return 是否允许返回值
     */
    boolean cacheGet(String key, Object value);
}
