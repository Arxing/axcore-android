package org.arxing.attrparser;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.annimon.stream.Stream;
import com.google.gson.JsonElement;

import org.arxing.attrparser.elements.Element;
import org.arxing.attrparser.getter.BoolGetter;
import org.arxing.attrparser.getter.ColorGetter;
import org.arxing.attrparser.getter.DimenGetter;
import org.arxing.attrparser.getter.DrawableGetter;
import org.arxing.attrparser.getter.FloatGetter;
import org.arxing.attrparser.getter.FractionGetter;
import org.arxing.attrparser.getter.IdGetter;
import org.arxing.attrparser.getter.IntGetter;
import org.arxing.attrparser.getter.JsonGetter;
import org.arxing.attrparser.getter.StringGetter;
import org.arxing.jparser.JParser;
import org.arxing.jparser.JsonSelector;

import java.util.List;


public class AttrParser {

    private static String clearSpace(String s) {
        return s.replace(" ", "");
    }

    private static boolean isValidArray(String s) {
        s = clearSpace(s);
        return s.startsWith("[") && s.endsWith("]");
    }

    private static String[] splitElements(String input) {
        input = clearSpace(input);
        if (!isValidArray(input))
            throw new Error();
        input = input.substring(1, input.length() - 1);
        return input.split(",");
    }

    public static <TGetter> List<TGetter> parseToGetterList(String input, Class<TGetter> type, @Element.ElementType int filter) {
        return Stream.of(splitElements(input))
                     .map(ElementParser::autoParse)
                     .flatMap(Stream::of)
                     .filter(el -> el.type() == filter)
                     .select(type)
                     .toList();
    }

    /*
     * List
     * */

    public static List<Boolean> parseToBoolList(Context context, String input) {
        return Stream.of(parseToGetterList(input, BoolGetter.class, Element.TYPE_BOOL)).map(getter -> getter.getBool(context)).toList();
    }

    public static List<Integer> parseToColorList(Context context, String input) {
        return Stream.of(parseToGetterList(input, ColorGetter.class, Element.TYPE_COLOR)).map(getter -> getter.getColor(context)).toList();
    }

    public static List<Float> parseToDimenList(Context context, String input) {
        return Stream.of(parseToGetterList(input, DimenGetter.class, Element.TYPE_DIMEN)).map(getter -> getter.getDimen(context)).toList();
    }

    public static List<Drawable> parseToDrawableList(Context context, String input) {
        return Stream.of(parseToGetterList(input, DrawableGetter.class, Element.TYPE_DRAWABLE))
                     .map(getter -> getter.getDrawable(context))
                     .toList();
    }

    public static List<Float> parseToFloatList(Context context, String input) {
        return Stream.of(parseToGetterList(input, FloatGetter.class, Element.TYPE_FLOAT)).map(getter -> getter.getFloat(context)).toList();
    }

    public static List<Float> parseToFractionList(Context context, String input) {
        return Stream.of(parseToGetterList(input, FractionGetter.class, Element.TYPE_FRACTION))
                     .map(getter -> getter.getFraction(context))
                     .toList();
    }

    public static List<Integer> parseToIdList(Context context, String input) {
        return Stream.of(parseToGetterList(input, IdGetter.class, Element.TYPE_ID)).map(getter -> getter.getId(context)).toList();
    }

    public static List<Integer> parseToIntList(Context context, String input) {
        return Stream.of(parseToGetterList(input, IntGetter.class, Element.TYPE_INT)).map(getter -> getter.getInt(context)).toList();
    }

    public static List<String> parseToStringList(Context context, String input) {
        return Stream.of(parseToGetterList(input, StringGetter.class, Element.TYPE_STRING))
                     .map(getter -> getter.getString(context))
                     .toList();
    }

    public static List<JsonElement> parseToJsonList(Context context, String input) {
        return Stream.of(parseToGetterList(input, JsonGetter.class, Element.TYPE_JSON)).map(getter -> getter.getJson(context)).toList();
    }

    /*
     * Array
     * */

    public static boolean[] parseToBoolArray(Context context, String input) {
        List<Boolean> list = parseToBoolList(context, input);
        boolean[] array = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] parseToColorArray(Context context, String input) {
        List<Integer> list = parseToColorList(context, input);
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static float[] parseToDimenArray(Context context, String input) {
        List<Float> list = parseToDimenList(context, input);
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static Drawable[] parseToDrawableArray(Context context, String input) {
        List<Drawable> list = parseToDrawableList(context, input);
        Drawable[] array = new Drawable[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static float[] parseToFloatArray(Context context, String input) {
        List<Float> list = parseToFloatList(context, input);
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static float[] parseToFractionArray(Context context, String input) {
        List<Float> list = parseToFractionList(context, input);
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] parseToIdArray(Context context, String input) {
        List<Integer> list = parseToIdList(context, input);
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] parseToIntArray(Context context, String input) {
        List<Integer> list = parseToIntList(context, input);
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static String[] parseToStringArray(Context context, String input) {
        List<String> list = parseToStringList(context, input);
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static JsonElement[] parseToJsonArray(Context context, String input) {
        List<JsonElement> list = parseToJsonList(context, input);
        JsonElement[] array = new JsonElement[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /*
     * Json
     * */

    public static JsonElement parseToJson(String input, String selectString) {
        return JsonSelector.selectOrNull(input, selectString);
    }

    public static JsonElement parseToJson(String input) {
        return JsonSelector.selectOrNull(input, "");
    }

    public static <T> T parseToJsonBean(String input, Class<T> type, String selectString) {
        return JParser.fromJsonOrNull(parseToJson(input, selectString), type);
    }

    public static <T> T parseToJsonBean(String input, Class<T> type) {
        return parseToJsonBean(input, type, "");
    }
}
