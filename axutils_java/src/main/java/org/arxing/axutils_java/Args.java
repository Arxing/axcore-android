package org.arxing.axutils_java;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Args {
    private Map<String, List<String>> map = new HashMap<>();

    private Args(String[] args) {
        Iterator<? extends String> iterator = Stream.of(args).iterator();
        String param = null;
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.startsWith("-") || s.startsWith("--")) {
                param = s;
                map.put(param, new ArrayList<>());
            } else {
                if (param != null)
                    map.get(param).add(s);
            }
        }
    }

    public static Args of(String[] args) {
        return new Args(args);
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }

    public List<String> getValues(String name) {
        return map.get(name);
    }

    public String get(String name) {
        return Stream.ofNullable(getValues(name)).findFirst().orElse(null);
    }

    public String getOr(String name, String def) {
        String r = get(name);
        return r == null ? def : r;
    }

    public List<String> popValues(String name) {
        List<String> result = getValues(name);
        map.remove(name);

        return result;
    }

    public String pop(String name) {
        String result = get(name);
        map.remove(name);
        return result;
    }

    public String popOr(String name, String def) {
        String result = get(name);
        map.remove(name);
        return result == null ? def : result;
    }
}
