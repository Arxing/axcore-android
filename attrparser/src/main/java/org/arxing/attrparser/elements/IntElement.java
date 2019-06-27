package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.IntGetter;
import org.arxing.attrparser.getter.StringGetter;


public class IntElement extends Element implements IntGetter, StringGetter {
    public IntElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_INT;
    }

    @Override public int getInt(Context context) {
        return Integer.parseInt(source());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
