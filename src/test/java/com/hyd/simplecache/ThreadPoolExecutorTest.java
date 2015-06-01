package com.hyd.simplecache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * (description)
 * created at 2015/1/22
 *
 * @author Yiding
 */
public class ThreadPoolExecutorTest {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("[" + Thread.currentThread().getName() + "] finished.");
            }
        };

        for (int i = 10; i > 0; i--) {
            executor.execute(runnable);
        }

        // executor 会保持进程开启，但不会阻止进程收到终止命令时结束
    }
}
