package org.arxing.axutils_java;

public class ThreadUtil {

    public static Thread runOnNewThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        return t;
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
