package org.arxing.attrparser.elements;

import android.support.annotation.IntDef;

import org.arxing.attrparser.ElementParser;
import org.arxing.attrparser.WrongPatternError;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public abstract class Element {
    public final static int TYPE_BOOL = 100;
    public final static int TYPE_COLOR = 101;
    public final static int TYPE_DIMEN = 102;
    public final static int TYPE_DRAWABLE = 103;
    public final static int TYPE_FLOAT = 104;
    public final static int TYPE_FRACTION = 105;
    public final static int TYPE_ID = 106;
    public final static int TYPE_INT = 107;
    public final static int TYPE_JSON = 108;
    public final static int TYPE_STRING = 109;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_BOOL, TYPE_COLOR, TYPE_DIMEN, TYPE_DRAWABLE, TYPE_FLOAT, TYPE_FRACTION, TYPE_ID, TYPE_INT, TYPE_JSON, TYPE_STRING})
    public @interface ElementType {
    }

    private String source;

    public Element(String source) {
        this.source = source;
        if (!isValid(source, pattern()))
            throw new WrongPatternError();
    }

    public String source() {
        return source;
    }

    public abstract @ElementType int type();

    public final String pattern() {
        return ElementParser.getPattern(getClass());
    }

    public boolean isValid(String source, String pattern) {
        return source.matches(pattern);
    }

    @Override public String toString() {
        return String.format("class=%s, source=%s, type=%d", getClass().getSimpleName(), source, type());
    }
}
