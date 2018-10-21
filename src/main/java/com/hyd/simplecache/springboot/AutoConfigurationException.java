package com.hyd.simplecache.springboot;

import com.hyd.simplecache.SimpleCacheException;

public class AutoConfigurationException extends SimpleCacheException {

    public AutoConfigurationException() {
    }

    public AutoConfigurationException(String message) {
        super(message);
    }

    public AutoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoConfigurationException(Throwable cause) {
        super(cause);
    }
}
