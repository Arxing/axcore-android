package org.arxing.attrparser.elements;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.arxing.attrparser.getter.DrawableGetter;


public class DrawableResElement extends ResElement implements DrawableGetter {
    public DrawableResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_DRAWABLE;
    }

    @Override public String resKind() {
        return "drawable";
    }

    @Override public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(getId(context));
    }
}
