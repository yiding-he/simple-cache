package com.hyd.simplecache.utils;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class LockFactoryTest {

    public static void main(String[] args) {
        new CheckThread("b").start();
        new CheckThread("a").start();
        new CheckThread("b").start();
        new CheckThread("c").start();
        new CheckThread("a").start();
        new CheckThread("b").start();
        new CheckThread("a").start();
        new CheckThread("c").start();
        new CheckThread("c").start();
    }

    /////////////////////////////////////////////////////////

    private static class CheckThread extends Thread {

        private String key;

        private CheckThread(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            System.out.println(String.format(
                    "Processing '%s' starting...", key));

            synchronized (LockFactory.getLock(key)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignored
                }

                long currSecond = System.currentTimeMillis() / 1000;
                System.out.println(String.format(
                        "Processing '%s' completed at %d.", key, currSecond));
            }
        }
    }
}
