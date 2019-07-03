package org.arxing.axutils_android;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrinterUtils {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final static Class[] PRIMITIVE_TYPES = {
            Integer.class, Long.class, Short.class, Byte.class, Float.class, Byte.class, Boolean.class, CharSequence.class
    };

    @SuppressWarnings("all") public static String parseParamVal(Object paramVal) {
        if (paramVal == null)
            return "null";
        Collector collector = Collectors.joining(", ");
        Class cls = paramVal.getClass();
        if (cls.isArray()) {
            List list = new ArrayList<>();
            for (int i = 0; i < Array.getLength(paramVal); i++) {
                Object el = Array.get(paramVal, i);
                list.add(el);
            }
            return parseParamVal(list);
        } else if (Collection.class.isAssignableFrom(cls)) {
            List list = (List) paramVal;
            String s = (String) Stream.of(list).map(PrinterUtils::parseParamVal).collect(collector);
            return "[" + s + "]";
        } else if (Map.class.isAssignableFrom(cls)) {
            Map map = (Map) paramVal;
            String s = (String) Stream.of(map).map(ent -> {
                Map.Entry entry = (Map.Entry) ent;
                String key = parseParamVal(entry.getKey());
                String val = parseParamVal(entry.getValue());
                return key + "=>" + val;
            }).collect(collector);
            return "{" + s + "}";
        } else if (cls.isPrimitive()) {
            return String.valueOf(paramVal);
        } else if (cls.equals(String.class)) {
            return "\"" + paramVal + "\"";
        } else if (Stream.of(PRIMITIVE_TYPES).anyMatch(o -> o.equals(cls))) {
            return String.valueOf(paramVal);
        }
        try {
            String json = gson.toJson(paramVal);
            String clsInfo = paramVal.getClass().getName();
            return clsInfo + ":" + json;
        } catch (Throwable e) {
            return paramVal.toString();
        }
    }
}
