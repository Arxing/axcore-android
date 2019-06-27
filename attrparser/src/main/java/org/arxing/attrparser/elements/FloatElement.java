package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.FloatGetter;
import org.arxing.attrparser.getter.StringGetter;


public class FloatElement extends Element implements FloatGetter, StringGetter {
    public FloatElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_FLOAT;
    }

    @Override public float getFloat(Context context) {
        return Float.parseFloat(source());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
