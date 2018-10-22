package com.hyd.simplecache;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建 CacheAdapter 对象的工厂类。
 */
public class CacheAdapterFactory {

    private static final Map<Class<? extends CacheConfiguration>, CacheAdapterCreator> CREATOR_MAP = new HashMap<>();

    public static <T extends CacheConfiguration> void register(Class<T> type, CacheAdapterCreator creator) {
        CREATOR_MAP.put(type, creator);
    }

    /**
     * 创建 CacheAdapter 对象
     *
     * @param configuration 缓存配置
     *
     * @return 创建的 CacheAdapter 对象
     */
    @SuppressWarnings("unchecked")
    static CacheAdapter createCacheAdapter(CacheConfiguration configuration) {

        for (Class<? extends CacheConfiguration> cacheConfigType : CREATOR_MAP.keySet()) {

            boolean isCompatibleType = cacheConfigType.isAssignableFrom(configuration.getClass());

            if (isCompatibleType) {
                CacheAdapterCreator adapterCreator = CREATOR_MAP.get(cacheConfigType);
                return adapterCreator.create(configuration);
            }
        }

        throw new IllegalArgumentException("Unrecognized configuration type: " + configuration.getClass());
    }

    ///////////////////////////////////////////////////////////////

    @FunctionalInterface
    public interface CacheAdapterCreator<T extends CacheConfiguration> {

        CacheAdapter create(T configuration);
    }

}
