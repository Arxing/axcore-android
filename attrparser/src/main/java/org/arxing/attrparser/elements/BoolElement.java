package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.BoolGetter;
import org.arxing.attrparser.getter.StringGetter;


public class BoolElement extends Element implements BoolGetter, StringGetter {
    public BoolElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_BOOL;
    }

    @Override public boolean getBool(Context context) {
        return Boolean.parseBoolean(source());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
