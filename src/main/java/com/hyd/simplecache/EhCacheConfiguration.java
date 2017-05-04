package com.hyd.simplecache;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class EhCacheConfiguration implements CacheConfiguration {

    public static final int DEFAULT_MAX_ENTRIES_LOCAL_HEAP = 1000;

    private net.sf.ehcache.config.CacheConfiguration configuration = new net.sf.ehcache.config.CacheConfiguration();

    public EhCacheConfiguration() {
        this.setName("ehcache" + this.hashCode());
        this.setMaxEntriesLocalHeap(DEFAULT_MAX_ENTRIES_LOCAL_HEAP);
    }

    public net.sf.ehcache.config.CacheConfiguration getConfiguration() {
        return configuration;
    }

    public void setCacheLoaderTimeoutMillis(long value) {
        configuration.setCacheLoaderTimeoutMillis(value);
    }

    public void setClearOnFlush(boolean value) {
        configuration.setClearOnFlush(value);
    }

    public void setCopyOnRead(boolean value) {
        configuration.setCopyOnRead(value);
    }

    public void setCopyOnWrite(boolean value) {
        configuration.setCopyOnWrite(value);
    }

    public void setDiskAccessStripes(int value) {
        configuration.setDiskAccessStripes(value);
    }

    public void setDiskExpiryThreadIntervalSeconds(long value) {
        configuration.setDiskExpiryThreadIntervalSeconds(value);
    }

    public void setDiskSpoolBufferSizeMB(int value) {
        configuration.setDiskSpoolBufferSizeMB(value);
    }

    public void setEternal(boolean value) {
        configuration.setEternal(value);
    }

    public void setLogging(boolean value) {
        configuration.setLogging(value);
    }

    public void setMaxBytesLocalDisk(long value) {
        configuration.setMaxBytesLocalDisk(value);
    }

    public void setMaxBytesLocalHeap(Long value) {
        configuration.setMaxBytesLocalHeap(value);
    }

    public void setMaxBytesLocalOffHeap(Long value) {
        configuration.setMaxBytesLocalOffHeap(value);
    }

    public void setMaxElementsOnDisk(int value) {
        configuration.setMaxElementsOnDisk(value);
    }

    public void setMaxEntriesLocalDisk(long value) {
        configuration.setMaxEntriesLocalDisk(value);
    }

    public void setMaxBytesLocalHeap(long value) {
        configuration.setMaxBytesLocalHeap(value);
    }

    public void setMaxBytesLocalHeap(String value) {
        configuration.setMaxBytesLocalHeap(value);
    }

    public void setMaxEntriesLocalHeap(long value) {
        configuration.setMaxEntriesLocalHeap(value);
    }

    public void setMaxBytesLocalOffHeap(long value) {
        configuration.setMaxBytesLocalOffHeap(value);
    }

    public void setMaxBytesLocalOffHeap(String value) {
        configuration.setMaxBytesLocalOffHeap(value);
    }

    /**
     * 设置缓存清除策略
     *
     * @param value 可选值：LRU/LFU/FIFO/CLOCK
     */
    public void setMemoryStoreEvictionPolicy(String value) {
        configuration.setMemoryStoreEvictionPolicy(value);
    }

    public void setName(String value) {
        configuration.setName(value);
    }

    public String getName() {
        return configuration == null ? null : configuration.getName();
    }

    public void setOverflowToOffHeap(boolean value) {
        configuration.setOverflowToOffHeap(value);
    }

    public void setStatistics(boolean value) {
        configuration.setStatistics(value);
    }

    public void setTimeToIdleSeconds(long value) {
        configuration.setTimeToIdleSeconds(value);
    }

    public void setTimeToLiveSeconds(long value) {
        configuration.setTimeToLiveSeconds(value);
    }

}
