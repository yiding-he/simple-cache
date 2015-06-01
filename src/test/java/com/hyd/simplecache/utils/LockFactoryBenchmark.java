package com.hyd.simplecache.utils;

/**
 * (description)
 * created at 2014/12/9
 *
 * @author Yiding
 */
public class LockFactoryBenchmark {

    public static void main(String[] args) {
        int i = 0, max = 10000000;
        long start = System.currentTimeMillis();
        while (i < max) {
            Object lock = LockFactory.getLock(String.valueOf(i));
            i++;
        }
        System.out.println(max + " locks generated in " + (System.currentTimeMillis() - start) + "ms");
    }
}
