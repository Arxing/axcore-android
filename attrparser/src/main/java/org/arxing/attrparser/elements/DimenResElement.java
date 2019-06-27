package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.DimenGetter;


public class DimenResElement extends ResElement implements DimenGetter {
    public DimenResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_DIMEN;
    }

    @Override public String resKind() {
        return "dimen";
    }

    @Override public float getDimen(Context context) {
        return context.getResources().getDimension(getId(context));
    }
}
