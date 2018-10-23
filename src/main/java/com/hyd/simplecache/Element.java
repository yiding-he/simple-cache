package com.hyd.simplecache;

/**
 * 对缓存元素的包装
 */
public class Element<E> {

    // DO NOT CHANGE THIS OR YOU MAY GET SERIALIZATION ERROR
    @SuppressWarnings("unused")
    public static final long serialVersionUID = -1239539875870833481L;

    private E value;            // 被缓存的实际值

    private long lastUpdate;    // 缓存生成时间

    private long expiry;        // 自定义的缓存超时时间，与缓存配置的超时时间无关

    public Element(E value) {
        this(value, System.currentTimeMillis(), 0);
    }

    public Element(E value, long lastUpdate) {
        this(value, lastUpdate, 0);
    }

    public Element(E value, long lastUpdate, long expiry) {
        this.value = value;
        this.lastUpdate = lastUpdate;
        this.expiry = expiry;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean expired() {
        return expiry > 0 && System.currentTimeMillis() - expiry > lastUpdate;
    }
}
