package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.IntGetter;


public class IntResElement extends ResElement implements IntGetter {
    public IntResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_INT;
    }

    @Override public String resKind() {
        return "integer";
    }

    @Override public int getInt(Context context) {
        return context.getResources().getInteger(getId(context));
    }
}
