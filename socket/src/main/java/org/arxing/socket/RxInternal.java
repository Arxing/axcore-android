package org.arxing.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.disposables.Disposable;

class RxInternal {
    private Map<String, List<Disposable>> map = new ConcurrentHashMap<>();
    private final String DEFAULT_NAME = "def";

    public void bind(String name, Disposable disposable) {
        if (!map.containsKey(name))
            map.put(name, new ArrayList<>());
        map.get(name).add(disposable);
    }

    public void bind(Class cls, Disposable disposable) {
        bind(cls.getName(), disposable);
    }

    public void bind(Object obj, Disposable disposable) {
        bind(obj.getClass(), disposable);
    }

    public void bind(Disposable disposable) {
        bind(DEFAULT_NAME, disposable);
    }

    public void unbindAll(String name) {
        if (map.containsKey(name)) {
            for (Disposable disposable : map.get(name)) {
                if (!disposable.isDisposed())
                    disposable.dispose();
            }
            map.remove(name);
        }
    }

    public void unbindAll(Class cls) {
        unbindAll(cls.getName());
    }

    public void unbindAll(Object obj) {
        unbindAll(obj.getClass());
    }

    public void unbindAll() {
        for (String name : map.keySet()) {
            unbindAll(name);
        }
    }
}
