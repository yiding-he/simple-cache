package com.hyd.simplecache.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分范围的计数器
 *
 * @author yiding.he
 */
public class IntervalCounter {

    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    private int[] splitters;

    private int[] counters;

    public IntervalCounter(int... splitters) {
        this.splitters = splitters;
        this.counters = new int[this.splitters.length + 1];
    }

    public void add(long value) {
        for (int i = splitters.length - 1; i >= 0; i--) {
            int splitter = splitters[i];

            if (value >= splitter) {
                counters[i + 1]++;
                break;
            } else if (i == 0) {
                counters[0]++;
            }
        }
    }

    public int[] getSplitters() {
        return splitters;
    }

    public int[] getCounters() {
        return counters;
    }

    public Map<String, Integer> getCountResult() {
        Map<String, Integer> counters = new LinkedHashMap<String, Integer>();
        int[] splitters = getSplitters();
        int[] counts = getCounters();

        for (int i = 0; i < splitters.length; i++) {
            int start = (i == 0) ? 0 : splitters[i - 1];
            int end = splitters[i];
            int count = counts[i];
            counters.put(start + "-" + end, count);
        }

        counters.put(splitters[splitters.length - 1] + "+", counts[counts.length - 1]);
        return counters;
    }

    public void start() {
        startTime.set(System.currentTimeMillis());
    }

    public void finish() {
        Long start = startTime.get();
        if (start == null) {
            return;
        }

        long end = System.currentTimeMillis();
        this.add(end - start);
    }

    ///////////////////////////////////////////////////////////////


    @Override
    public String toString() {
        return getCountResult().toString();
    }
}
