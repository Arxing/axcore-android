package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.BoolGetter;

public class BoolResElement extends ResElement implements BoolGetter {
    public BoolResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_BOOL;
    }

    @Override public String resKind() {
        return "bool";
    }

    @Override public boolean getBool(Context context) {
        return context.getResources().getBoolean(getId(context));
    }
}
