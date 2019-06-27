package org.arxing.jparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class JElementHelper {
    private JsonElement element;

    private JElementHelper(JsonElement element) {
        this.element = element;
    }

    public static JElementHelper of(JsonElement element) {
        return new JElementHelper(element);
    }

    public boolean isJsonObject() {
        return element.isJsonObject();
    }

    public boolean isJsonArray() {
        return element.isJsonArray();
    }

    public boolean isJsonNull() {
        return element.isJsonNull();
    }

    public boolean isJsonPrimitive() {
        return element.isJsonPrimitive();
    }

    public boolean isString() {
        return isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    public boolean isBoolean() {
        return isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }

    public boolean isNumber() {
        return isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    public boolean isBean(Type type) {
        try {
            JParser.fromJsonOrNull(element, type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JsonObject getAsJsonObject() {
        return element.getAsJsonObject();
    }

    public JsonArray getAsJsonArray() {
        return element.getAsJsonArray();
    }

    public JsonPrimitive getAsJsonPrimitive() {
        return element.getAsJsonPrimitive();
    }

    public Number getAsNumber() {
        return element.getAsNumber();
    }

    public <T> T getAsBean(Type type) {
        return JParser.fromJsonOrNull(element, type);
    }

}
