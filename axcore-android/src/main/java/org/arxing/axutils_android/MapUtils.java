package org.arxing.axutils_android;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static <TKey, TValue> SimpleMap<TKey, TValue> newMap() {
        return new SimpleMap<>();
    }

    public static <TValue> SimpleMap<String, TValue> newKeyedMap() {
        return new SimpleMap<>();
    }

    public static SimpleMap<Class, Object> newTypedMap() {
        return new SimpleMap<>();
    }

    public static <TValue> SimpleMap<Integer, TValue> newIndexedMap() {
        return new SimpleMap<>();
    }

    public static <TValue> SimpleMap<Object, TValue> newObjectedMap() {
        return new SimpleMap<>();
    }

    public static <TK, TV> SimpleMap of(Map<TK, TV> map) {
        SimpleMap<TK, TV> result = new SimpleMap<>();
        result.putAll(map);
        return result;
    }

    public static class SimpleMap<TKey, TValue> extends HashMap<TKey, TValue> {

        public SimpleMap<TKey, TValue> add(TKey key, TValue value) {
            put(key, value);
            return this;
        }

        public SimpleMap<TKey, TValue> delete(TKey key) {
            remove(key);
            return this;
        }

        public <T> T getTo(TKey key) {
            return (T) super.get(key);
        }
    }
}
