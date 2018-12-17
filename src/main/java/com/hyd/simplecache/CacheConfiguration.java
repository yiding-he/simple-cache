package com.hyd.simplecache;

import com.hyd.simplecache.utils.Str;

/**
 * 缓存配置。这个接口之所以没有任何方法，是因为不同的缓存库有不同的
 * 配置类，且 {@link CacheAdapterFactory} 对缓存配置没有要求特定的方法。
 */
public interface CacheConfiguration {

    default String getType() {
        return Str.removeEnd(this.getClass().getSimpleName(), "Configuration");
    }
}
