package org.arxing.attrparser.elements;

public class StringResElement extends ResElement {

    public StringResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_STRING;
    }

    @Override public String resKind() {
        return "string";
    }
}
