package com.hyd.simplecache.serialization;

/**
 * 系统预定义的序列化类型。CacheAdapter 的实现类如果在存取缓存时
 * 包含序列化过程，可以考虑是否支持用户选择序列化方式。
 */
public enum PredefinedSerializeMethod {

    FST((byte)0),
    JSON((byte) 1),

    ;

    private byte tag;

    PredefinedSerializeMethod(byte tag) {

        this.tag = tag;
    }

    public byte getTag() {
        return tag;
    }
}
