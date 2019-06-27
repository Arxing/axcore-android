package org.arxing.axutils_android;


import android.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
    public final static Logger defLogger = new Logger("def");
    private static boolean globalEnabled = true;
    private String tag;
    private boolean enable = true;
    private boolean showLocation = false;

    public Logger(String tag) {
        this(tag, true);
    }

    public Logger(String tag, boolean enable) {
        this.tag = tag;
        this.enable = enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public void w(String format, Object... objs) {
        internalPrint(Log.WARN, format, objs);
    }

    public void i(String format, Object... objs) {
        internalPrint(Log.INFO, format, objs);
    }

    public void e(String format, Object... objs) {
        internalPrint(Log.ERROR, format, objs);
    }

    public void d(String format, Object... objs) {
        internalPrint(Log.DEBUG, format, objs);
    }

    public void v(String format, Object... objs) {
        internalPrint(Log.VERBOSE, format, objs);
    }

    public <TCollection extends Collection<TData>, TData> void collection(int level,
                                                                          TCollection collection,
                                                                          ObjectInfoTransfer<TData> transfer) {
        for (TData data : collection) {
            internalPrint(level, "%s", transfer.info(data));
        }
    }

    public <TCollection extends Collection<TData>, TData> void collection(int level, TCollection collection) {
        collection(level, collection, Object::toString);
    }

    public <TCollection extends Collection<TData>, TData> void collection(TCollection collection, ObjectInfoTransfer<TData> transfer) {
        collection(Log.DEBUG, collection, transfer);
    }

    public <TCollection extends Collection<TData>, TData> void collection(TCollection collection) {
        collection(Log.DEBUG, collection, Object::toString);
    }

    public <TMap extends Map<TKey, TVal>, TKey, TVal> void map(int level,
                                                               TMap map,
                                                               ObjectInfoTransfer<TKey> keyTransfer,
                                                               ObjectInfoTransfer<TVal> valTransfer) {
        for (Map.Entry<TKey, TVal> entry : map.entrySet()) {
            TKey key = entry.getKey();
            TVal val = entry.getValue();
            internalPrint(level, "%s -> %s", keyTransfer.info(key), valTransfer.info(val));
        }
    }

    public <TMap extends Map<TKey, TVal>, TKey, TVal> void map(int level, TMap map) {
        map(level, map, Object::toString, Object::toString);
    }

    public <TMap extends Map<TKey, TVal>, TKey, TVal> void map(TMap map,
                                                               ObjectInfoTransfer<TKey> keyTransfer,
                                                               ObjectInfoTransfer<TVal> valTransfer) {
        map(Log.DEBUG, map, keyTransfer, valTransfer);
    }

    public <TMap extends Map<TKey, TVal>, TKey, TVal> void map(TMap map) {
        map(Log.DEBUG, map, Object::toString, Object::toString);
    }

    private void internalPrint(int level, String format, Object... objs) {
        if (!enable || !globalEnabled)
            return;
        ThreadUtil.sleep(1);
        String content, realTag;
        if (showLocation) {
            content = String.format("[%s] : %s", tag, String.format(format, objs));
            realTag = getCurrentClass(3) + ":" + getCurrentMethod(3);
        } else {
            content = String.format(format, objs);
            realTag = tag;
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(realTag, content);
                break;
            case Log.DEBUG:
                Log.d(realTag, content);
                break;
            case Log.WARN:
                Log.w(realTag, content);
                break;
            case Log.ERROR:
                Log.e(realTag, content);
                break;
            case Log.INFO:
                Log.i(realTag, content);
                break;
        }
    }

    private static String getCurrentClass(int depth) {
        StackTraceElement[] stacks = new Exception().getStackTrace();
        StackTraceElement stack = stacks[depth];
        String[] splits = stack.getClassName().split("\\.");
        String fullClassName = splits[splits.length - 1];
        return fullClassName.split("\\$")[0];
    }

    private static String getCurrentMethod(int depth) {
        StackTraceElement[] stacks = new Exception().getStackTrace();
        StackTraceElement stack = stacks[depth];
        return stack.getMethodName();
    }

    public static void setGlobalEnabled(boolean enable) {
        globalEnabled = enable;
    }

    public interface ObjectInfoTransfer<TData> {
        String info(TData data);
    }



    /*
     * timing
     * */

    private static Map<String, Long> timingMap = new ConcurrentHashMap<>();
    private final static String defTimingTag = "Timing";

    public static void startTiming() {
        timingMap.put(defTimingTag, System.currentTimeMillis());
    }

    public static void startTiming(String tag) {
        timingMap.put(tag, System.currentTimeMillis());
    }

    public static void endTiming() {
        endTiming(defTimingTag);
    }

    public static void endTiming(String tag) {
        long t = timingMap.remove(tag);
        t = System.currentTimeMillis() - t;
        defLogger.d("%s cost %dms", tag, t);
    }

    public static void breakTiming(String tag) {
        long t = timingMap.get(tag);
        t = System.currentTimeMillis() - t;
        defLogger.d("%s break %dms", tag, t);
    }

    public static void breakTiming() {
        breakTiming(defTimingTag);
    }

    public static void breakTimingMsg(String tag, String format, Object... objs) {
        long t = timingMap.get(tag);
        t = System.currentTimeMillis() - t;
        defLogger.d("%s break %s %dms", tag, String.format(format, objs), t);
    }

    public static void breakTimingMsg(String format, Object... objs) {
        breakTimingMsg(defTimingTag, format, objs);
    }
}
