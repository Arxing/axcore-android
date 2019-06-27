package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.StringGetter;


public class StringElement extends Element implements StringGetter {

    public StringElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_STRING;
    }

    @Override public String getString(Context context) {
        return source();
    }
}
