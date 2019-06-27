package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.DimensionConverter;
import org.arxing.attrparser.getter.DimenGetter;
import org.arxing.attrparser.getter.StringGetter;


public class DimenElement extends Element implements DimenGetter, StringGetter {
    public DimenElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_DIMEN;
    }

    @Override public float getDimen(Context context) {
        return DimensionConverter.stringToDimension(source(), context.getResources().getDisplayMetrics());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
