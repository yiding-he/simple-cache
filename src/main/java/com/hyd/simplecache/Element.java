package com.hyd.simplecache;

import java.io.Serializable;

/**
 * 对缓存元素的包装
 *
 * @author 贺一丁
 */
public class Element<E extends Serializable> implements Serializable {

    // 这个值不要动，动了就会导致取不到数据
    public static final long serialVersionUID = -1239539875870833481L;

    private E value;

    private long lastUpdate;

    public Element(E value) {
        this(value, System.currentTimeMillis());
    }

    public Element(E value, long lastUpdate) {
        this.value = value;
        this.lastUpdate = lastUpdate;
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
}
