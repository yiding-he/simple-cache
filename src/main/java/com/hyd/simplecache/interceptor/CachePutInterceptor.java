package com.hyd.simplecache.interceptor;

public interface CachePutInterceptor {

    /**
     * 拦截 put 操作
     *
     * @param key   键
     * @param value 值
     *
     * @return 是否允许 put 进行
     */
    boolean cachePut(String key, Object value);
}
