package org.arxing.rx_utils;

import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Rx {
    private static Map<String, List<Disposable>> map = new ConcurrentHashMap<>();
    private final static String DEFAULT_NAME = "def";

    public static void bind(String name, Disposable disposable) {
        if (!map.containsKey(name))
            map.put(name, new ArrayList<>());
        map.get(name).add(disposable);
    }

    public static void bind(Class cls, Disposable disposable) {
        bind(cls.getName(), disposable);
    }

    public static void bind(Object obj, Disposable disposable) {
        bind(obj.getClass(), disposable);
    }

    public static void bind(Disposable disposable) {
        bind(DEFAULT_NAME, disposable);
    }

    public static void unbindAll(String name) {
        for (Disposable disposable : map.get(name)) {
            if (!disposable.isDisposed())
                disposable.dispose();
        }
        map.remove(name);
    }

    public static void unbindAll(Class cls) {
        unbindAll(cls.getName());
    }

    public static void unbindAll(Object obj) {
        unbindAll(obj.getClass());
    }

    public static void unbindAll() {
        for (String name : map.keySet()) {
            unbindAll(name);
        }
    }
}
