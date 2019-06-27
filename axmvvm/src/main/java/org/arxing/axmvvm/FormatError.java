package org.arxing.axmvvm;

public class FormatError extends Error {

    public FormatError(String format, Object... objs) {
        super(String.format(format, objs));
    }
}
