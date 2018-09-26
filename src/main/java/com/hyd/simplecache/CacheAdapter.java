package com.hyd.simplecache;

import java.util.Iterator;

/**
 * 缓存通用接口
 *
 * @author 贺一丁
 */
public interface CacheAdapter {

    /**
     * 获取缓存配置
     *
     * @return 缓存配置
     */
    public CacheConfiguration getConfiguration();

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     *
     * @return 缓存值
     */
    Object get(String key);

    /**
     * 设置缓存值
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param forever 是否永久保存
     */
    void put(String key, Object value, boolean forever);

    /**
     * 设置缓存值
     *
     * @param key               缓存键
     * @param value             缓存值
     * @param timeToLiveSeconds 本条缓存的保存时长（秒）
     */
    void put(String key, Object value, int timeToLiveSeconds);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 清空当前缓存
     */
    void clear();

    /**
     * 关闭缓存客户端对象，释放相关资源
     */
    void dispose();

    /**
     * 列出所有 key
     *
     * @return 所有 key
     *
     * @throws UnsupportedOperationException 如果缓存不支持该操作
     */
    Iterator<String> keys() throws UnsupportedOperationException;

    /**
     * 判断 key 是否存在。如果是为了取值，请直接把值取过来再判断是否为空。
     *
     * @param key 要查找的 key
     *
     * @return 如果 key 存在则返回 true
     */
    boolean keyExists(String key);

    void touch(String key);
}
