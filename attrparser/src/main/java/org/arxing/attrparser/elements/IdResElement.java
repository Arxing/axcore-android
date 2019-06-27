package org.arxing.attrparser.elements;

public class IdResElement extends ResElement {
    public IdResElement(String source) {
        super(source);
    }

    @Override public int type() {
        return TYPE_ID;
    }

    @Override public String resKind() {
        return "id";
    }
}
