package org.arxing.axutils_java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Type;

public class JParser {
    private static Gson defaultGson;
    private static Gson prettyGson;
    private static JsonParser jsonParser;

    static {
        defaultGson = new GsonBuilder().create();
        prettyGson = new GsonBuilder().setPrettyPrinting().create();
        jsonParser = new JsonParser();
    }

    // to json

    public static String toJson(Object obj) {
        return defaultGson.toJson(obj);
    }

    public static String toPrettyJson(Object obj) {
        return prettyGson.toJson(obj);
    }

    // from json

    private static <T> T fromJson(Gson gson, JsonElement element, Type type) throws Exception {
        return gson.fromJson(element, type);
    }

    public static <T> T fromJson(JsonElement element, Type type) throws Exception {
        return fromJson(defaultGson, element, type);
    }

    public static <T> T fromJson(JsonElement element, Type type, TypeAdapterFactory factory) throws Exception {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(factory).create();
        return fromJson(gson, element, type);
    }

    public static <T> T fromJson(String s, Type type) throws Exception {
        return fromJson(parse(s), type);
    }

    public static <T> T fromJson(String s, Type type, TypeAdapterFactory factory) throws Exception {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(factory).create();
        return fromJson(gson, parse(s), type);
    }

    public static <T> T fromJsonOrDefault(JsonElement element, Type type, T defValue) {
        try {
            return fromJson(element, type);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static <T> T fromJsonOrDefault(JsonElement element, Type type, T defValue, TypeAdapterFactory factory) throws Exception {
        try {
            return fromJson(element, type, factory);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static <T> T fromJsonOrDefault(String s, Type type, T defValue) {
        return fromJsonOrDefault(parse(s), type, defValue);
    }

    public static <T> T fromJsonOrDefault(String s, Type type, T defValue, TypeAdapterFactory factory) throws Exception {
        return fromJsonOrDefault(parse(s), type, defValue, factory);
    }

    public static <T> T fromJsonOrNull(JsonElement element, Type type) {
        return fromJsonOrDefault(element, type, null);
    }

    public static <T> T fromJsonOrNull(JsonElement element, Type type, TypeAdapterFactory factory) throws Exception {
        return fromJsonOrDefault(element, type, null, factory);
    }

    public static <T> T fromJsonOrNull(String s, Type type) {
        return fromJsonOrNull(parse(s), type);
    }

    public static <T> T fromJsonOrNull(String s, Type type, TypeAdapterFactory factory) throws Exception {
        return fromJsonOrNull(parse(s), type, factory);
    }

    // parse

    public static JsonElement parse(Object src) {
        return parse(toJson(src));
    }

    public static JsonElement parse(String s) {
        return jsonParser.parse(s);
    }

    public static boolean isJson(String s) {
        try {
            parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
