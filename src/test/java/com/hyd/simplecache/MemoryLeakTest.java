package com.hyd.simplecache;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class MemoryLeakTest {

    public static final String KEY_PREFIX =
            "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-" +
                    "key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-key-";

    public static final String VALUE_PREFIX =
            "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-" +
                    "value-value-value-value-value-value-value-value-value-value-value-";

    private static int counter;

    public static void main(String[] args) {
        new MemoryMonitorThread().start();

        EhCacheConfiguration conf = new EhCacheConfiguration();
        conf.setMaxEntriesLocalHeap(100000);

        SimpleCache simpleCache = new SimpleCache(conf);

        while (true) {
            simpleCache.put(KEY_PREFIX + counter, VALUE_PREFIX + counter);
            counter++;
        }
    }

    public static void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            // ignore this
        }
    }

    /////////////////////////////////////////////////////////

    private static class MemoryMonitorThread extends Thread {

        @Override
        public void run() {
            while (true) {
                outpurMemoryInfo();
                MemoryLeakTest.sleep(1500);
            }
        }

        private void outpurMemoryInfo() {
            Runtime runtime = Runtime.getRuntime();

            long free = runtime.freeMemory() / 1024;
            long total = runtime.totalMemory() / 1024;
            double percentage = (double) free / total * 100;

            System.out.printf(
                    "%,dk/%,dk used, %.2f%% free, counter=%d\n",
                    (total - free), total, percentage, counter);
        }
    }
}
