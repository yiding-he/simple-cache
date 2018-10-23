package com.hyd.simplecache;

/**
 * SimpleCache 相关异常
 */
public class SimpleCacheException extends RuntimeException {

    public SimpleCacheException() {
    }

    public SimpleCacheException(String message) {
        super(message);
    }

    public SimpleCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleCacheException(Throwable cause) {
        super(cause);
    }
}
