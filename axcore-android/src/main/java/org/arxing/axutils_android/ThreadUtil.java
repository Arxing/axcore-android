package org.arxing.axutils_android;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
    public static boolean isInMainThread() {
        Looper currentLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return currentLooper != null && currentLooper.getThread().equals(mainLooper.getThread());
    }

    public static Thread runOnNewThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        return t;
    }

    public static void post(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void postDelay(Runnable runnable, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
