package org.arxing.attrparser.elements;

import android.content.Context;

import com.google.gson.JsonElement;

import org.arxing.attrparser.getter.JsonGetter;
import org.arxing.attrparser.getter.StringGetter;
import org.arxing.jparser.JParser;


public class JElement extends Element implements JsonGetter, StringGetter {
    public JElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_JSON;
    }

    @Override public boolean isValid(String source, String pattern) {
        return JParser.isJson(source);
    }

    @Override public JsonElement getJson(Context context) {
        return JParser.parse(source());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
