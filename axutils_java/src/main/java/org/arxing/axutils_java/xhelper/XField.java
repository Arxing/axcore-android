package org.arxing.axutils_java.xhelper;


import org.arxing.axutils_java.function.Consumer;
import org.arxing.axutils_java.function.Function;
import org.arxing.axutils_java.function.Optional;

public class XField<T> {
    private T value;
    private Throwable error;
    private Consumer<Throwable> errorHandler;

    private XField(T value) {
        this.value = value;
        if (value == null)
            error = new NullPointerException();
    }

    public static <T> XField<T> of(T t) {
        return new XField<>(t);
    }

    public XField<T> occurError(Throwable error) {
        this.error = error;
        return this;
    }

    public XField<T> occurError(String format, Object... objects) {
        return occurError(new Throwable(String.format(format, objects)));
    }

    public XField<T> occurError() {
        return occurError("no message.");
    }

    public <R> XField<R> map(Function<T, R> function) {
        XField<R> xField;
        if (value != null) {
            try {
                R result = function.apply(value);
                xField = of(result);
            } catch (Throwable e) {
                xField = of((R) null).occurError(e);
            }
        } else {
            xField = of(null);
        }
        return xField;
    }

    public void call(Consumer<T> consumer) {
        if (error == null) {
            try {
                consumer.apply(value);
            } catch (Throwable t) {
                error = t;
            }
        }
        if (errorHandler != null && error != null)
            errorHandler.apply(error);
    }

    public <R> Optional<R> callReturn(Function<T, R> function) {
        R result = null;
        if (error == null) {
            try {
                result = function.apply(value);
            } catch (Throwable t) {
                error = t;
            }
        }
        if (errorHandler != null && error != null)
            errorHandler.apply(error);
        return Optional.of(result);
    }

    public XField<T> doOnError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public T value() {
        return value;
    }
}
