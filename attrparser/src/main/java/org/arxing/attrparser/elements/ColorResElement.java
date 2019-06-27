package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.getter.ColorGetter;


public class ColorResElement extends ResElement implements ColorGetter {

    public ColorResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_COLOR;
    }

    @Override public String resKind() {
        return "color";
    }

    @Override public int getColor(Context context) {
        return context.getResources().getColor(getId(context));
    }
}
