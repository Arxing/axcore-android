package org.arxing.axmvvm.xhelper;

import org.arxing.utils.function.Consumer;
import org.arxing.utils.function.Function;

public class XRun<T> {
    private T host;

    private XRun(T host) {
        this.host = host;
    }

    public static <T> XRun<T> of(T t) {
        return new XRun<>(t);
    }

    public <R> XRun<R> map(Function<T, R> function) {
        R result = null;
        try {
            result = function.apply(host);
        } catch (Exception e) {
        }
        return of(result);
    }

    public void call(Consumer<T> consumer) {
        if (host != null)
            consumer.apply(host);
    }
}
