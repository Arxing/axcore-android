package org.arxing.axutils_java.function;


import org.arxing.axutils_java.AssertUtils;

public class Optional<T> {
    private T value;
    private Throwable error;

    private Optional(T value) {
        this.value = value;
        if (value == null)
            occurError(new NullPointerException());
    }

    public static <T> Optional<T> of(T host) {
        return new Optional<>(host);
    }

    public Optional<T> occurError(Throwable error) {
        this.error = error;
        return this;
    }

    public Optional<T> occurError(String format, Object... objects) {
        return occurError(new Throwable(String.format(format, objects)));
    }

    public Optional<T> occurError() {
        return occurError("no message.");
    }

    public T orElse(T defValue) {
        return value == null || error != null ? defValue : value;
    }

    public T orElseThrows() {
        AssertUtils.error(error != null, new Error(error));
        return value;
    }
}
