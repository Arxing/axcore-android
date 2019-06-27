package org.arxing.attrparser.elements;

import android.content.Context;

import org.arxing.attrparser.WrongPatternError;
import org.arxing.attrparser.getter.IdGetter;
import org.arxing.attrparser.getter.StringGetter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ResElement extends Element implements IdGetter, StringGetter {
    private String resName;

    public ResElement(String source) {
        super(source);
        Matcher matcher = Pattern.compile(pattern()).matcher(source);
        if (matcher.find()) {
            resName = matcher.group(1);
        } else {
            throw new WrongPatternError();
        }
    }

    public String resName() {
        return resName;
    }

    public abstract String resKind();

    @Override public int getId(Context context) {
        return context.getResources().getIdentifier(resName(), resKind(), context.getPackageName());
    }

    @Override public String getString(Context context) {
        return source();
    }
}
