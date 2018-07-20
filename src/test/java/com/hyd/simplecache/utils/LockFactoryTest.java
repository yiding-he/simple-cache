package com.hyd.simplecache.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class LockFactoryTest {

    private static Map<String, Integer> map = new HashMap<String, Integer>();

    public static void main(String[] args) {

        map.put("A", 0);

        for (int i = 0; i < 50; i++) {
            new CheckThread("A").start();
        }
    }

    /////////////////////////////////////////////////////////

    private static class CheckThread extends Thread {

        private String key;

        private CheckThread(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                sleep();
                synchronized (LockFactory.getLock(key)) {
                    doSynchronizedThing();
                }
            }
        }

        private void doSynchronizedThing() {
            Integer i = map.get(key);
            System.out.println("i = " + i);
            map.put(key, i + 1);
        }

        private void sleep() {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }
}
