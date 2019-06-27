package org.arxing.attrparser.elements;

import android.content.Context;
import android.graphics.Color;

import org.arxing.attrparser.getter.ColorGetter;
import org.arxing.attrparser.getter.StringGetter;


public class ColorElement extends Element implements ColorGetter, StringGetter {

    public ColorElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_COLOR;
    }

    @Override public int getColor(Context context) {
        return Color.parseColor(source());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
