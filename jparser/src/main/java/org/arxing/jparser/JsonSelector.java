package org.arxing.jparser;

import com.google.gson.JsonElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonSelector {
    private final static String EL_REGEX = "(?:\\.(\\w+))((\\[\\d+])*)|((\\[\\d+])+)";
    private final static String INDEX_REGEX = "\\[(\\d+)]";

    public static JsonElement selectOrNull(String json, String selectString) {
        return selectOrNull(JParser.parse(json), selectString);
    }

    public static JsonElement selectOrNull(JsonElement jsonElement, String selectString) {
        try {
            return select(jsonElement, selectString);
        } catch (JsonRouteException e) {
            return null;
        }
    }

    public static JsonElement select(String json, String selectString) throws JsonRouteException {
        return select(JParser.parse(json), selectString);
    }

    public static JsonElement select(JsonElement jsonElement, String selectString) throws JsonRouteException {
        Matcher matcher = Pattern.compile(EL_REGEX).matcher(selectString);
        JsonElement target = jsonElement;
        while (matcher.find()) {
            String key = matcher.group(1);
            String indexes = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);

            if (key != null) {
                if (!target.isJsonObject())
                    throw new JsonRouteException();
                target = target.getAsJsonObject().get(key);
            }

            if (indexes != null) {
                Matcher indexMatcher = Pattern.compile(INDEX_REGEX).matcher(indexes);
                while (indexMatcher.find()) {
                    String sIndex = indexMatcher.group(1);
                    int index = Integer.parseInt(sIndex);
                    if (!target.isJsonArray())
                        throw new JsonRouteException();
                    target = target.getAsJsonArray().get(index);
                }
            }
        }
        return target;
    }

    public static class JsonRouteException extends Exception {
    }
}
