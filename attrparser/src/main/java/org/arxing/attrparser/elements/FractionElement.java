package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.FractionGetter;
import org.arxing.attrparser.getter.StringGetter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FractionElement extends Element implements FractionGetter, StringGetter {
    public FractionElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_FRACTION;
    }

    @Override public float getFraction(Context context) {
        Matcher matcher = Pattern.compile(pattern()).matcher(source());
        if (matcher.find())
            return Float.parseFloat(matcher.group(1)) / 100f;
        return 0f;
    }

    @Override public String getString(Context context) {
        return source();
    }
}
