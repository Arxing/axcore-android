package org.arxing.attrparser;

import android.graphics.Color;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.arxing.attrparser.elements.BoolElement;
import org.arxing.attrparser.elements.BoolResElement;
import org.arxing.attrparser.elements.ColorElement;
import org.arxing.attrparser.elements.ColorResElement;
import org.arxing.attrparser.elements.DimenElement;
import org.arxing.attrparser.elements.DimenResElement;
import org.arxing.attrparser.elements.DrawableResElement;
import org.arxing.attrparser.elements.Element;
import org.arxing.attrparser.elements.FloatElement;
import org.arxing.attrparser.elements.FractionElement;
import org.arxing.attrparser.elements.IdResElement;
import org.arxing.attrparser.elements.IntElement;
import org.arxing.attrparser.elements.IntResElement;
import org.arxing.attrparser.elements.JElement;
import org.arxing.attrparser.elements.StringElement;
import org.arxing.attrparser.elements.StringResElement;
import org.arxing.jparser.JParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElementParser {
    private static Map<Class<? extends Element>, String> patternMap = new HashMap<>();
    private static String colorNamePattern;

    static {
        initColorNameMap();

        patternMap.put(BoolResElement.class, "@bool/(\\w+)");
        patternMap.put(ColorResElement.class, "@color/(\\w+)");
        patternMap.put(DimenResElement.class, "@dimen/(\\w+)");
        patternMap.put(DrawableResElement.class, "@drawable/(\\w+)");
        patternMap.put(IdResElement.class, "@id/(\\w+)");
        patternMap.put(IntResElement.class, "@integer/(\\w+)");
        patternMap.put(StringResElement.class, "@string/(\\w+)");
        patternMap.put(BoolElement.class, "true|false");
        patternMap.put(ColorElement.class, colorNamePattern + "|" + "#[0-9a-fA-F]{6,8}");
        patternMap.put(DimenElement.class, "-?\\d+(.\\d+)?(dp|sp|pt|px|mm|in)");
        patternMap.put(IntElement.class, "-?\\d+");
        patternMap.put(StringElement.class, "(.|\n|\r)*");
        patternMap.put(JElement.class, "");
        patternMap.put(FractionElement.class, "(-?\\d+(\\.\\d+)?)%");
        patternMap.put(FloatElement.class, "(-?\\d+(\\.\\d+)?f?)");
    }

    private static void initColorNameMap() {
        HashMap<String, Integer> sColorNameMap = new HashMap<>();
        sColorNameMap.put("black", Color.BLACK);
        sColorNameMap.put("darkgray", Color.DKGRAY);
        sColorNameMap.put("gray", Color.GRAY);
        sColorNameMap.put("lightgray", Color.LTGRAY);
        sColorNameMap.put("white", Color.WHITE);
        sColorNameMap.put("red", Color.RED);
        sColorNameMap.put("green", Color.GREEN);
        sColorNameMap.put("blue", Color.BLUE);
        sColorNameMap.put("yellow", Color.YELLOW);
        sColorNameMap.put("cyan", Color.CYAN);
        sColorNameMap.put("magenta", Color.MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", Color.DKGRAY);
        sColorNameMap.put("grey", Color.GRAY);
        sColorNameMap.put("lightgrey", Color.LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);

        colorNamePattern = Stream.of(sColorNameMap.keySet()).collect(Collectors.joining("|"));
    }

    public static String getPattern(Class<? extends Element> elementType) {
        if (patternMap.containsKey(elementType))
            return patternMap.get(elementType);
        else
            throw new UnsupportElementException();
    }

    public static List<Element> autoParse(String source) {
        List<Element> maybe = new ArrayList<>();
        if (source.matches(patternMap.get(FloatElement.class)))
            maybe.add(new FloatElement(source));
        if (source.matches(patternMap.get(BoolResElement.class)))
            maybe.add(new BoolResElement(source));
        if (source.matches(patternMap.get(ColorResElement.class)))
            maybe.add(new ColorResElement(source));
        if (source.matches(patternMap.get(DimenResElement.class)))
            maybe.add(new DimenResElement(source));
        if (source.matches(patternMap.get(DrawableResElement.class)))
            maybe.add(new DrawableResElement(source));
        if (source.matches(patternMap.get(IdResElement.class)))
            maybe.add(new IdResElement(source));
        if (source.matches(patternMap.get(IntResElement.class)))
            maybe.add(new IntResElement(source));
        if (source.matches(patternMap.get(StringResElement.class)))
            maybe.add(new StringResElement(source));
        if (source.matches(patternMap.get(BoolElement.class)))
            maybe.add(new BoolElement(source));
        if (source.matches(patternMap.get(ColorElement.class)))
            maybe.add(new ColorElement(source));
        if (source.matches(patternMap.get(DimenElement.class)))
            maybe.add(new DimenElement(source));
        if (source.matches(patternMap.get(IntElement.class)))
            maybe.add(new IntElement(source));
        if (source.matches(patternMap.get(FractionElement.class)))
            maybe.add(new FractionElement(source));
        if (JParser.isJson(source))
            maybe.add(new JElement(source));
        if (source.matches(patternMap.get(StringElement.class)))
            maybe.add(new StringElement(source));
        return maybe;
    }
}
