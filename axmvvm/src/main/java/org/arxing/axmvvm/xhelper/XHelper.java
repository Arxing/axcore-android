package org.arxing.axmvvm.xhelper;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableByte;
import android.databinding.ObservableChar;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.databinding.ObservableShort;

import org.arxing.utils.AssertUtils;
import org.arxing.utils.function.Function;
import org.arxing.utils.function.Optional;


public class XHelper<T> {
    private T host;
    private Throwable error;

    private XHelper(T host) {
        this.host = host;
    }

    public static <T> XHelper<T> of(ObservableField<T> data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Integer> of(ObservableInt data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Long> of(ObservableLong data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Boolean> of(ObservableBoolean data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Double> of(ObservableDouble data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Float> of(ObservableFloat data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Byte> of(ObservableByte data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Character> of(ObservableChar data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public static XHelper<Short> of(ObservableShort data) {
        AssertUtils.error(data == null, "data can not be null.");
        return new XHelper<>(data.get());
    }

    public <R> XHelper<R> map(Function<T, R> function) {
        ObservableField<R> field = new ObservableField<>();
        Throwable error = null;
        try {
            R result = function.apply(host);
            field.set(result);
        } catch (Throwable e) {
            error = e;
        }
        XHelper<R> nextHelper = of(field);
        nextHelper.error = error;
        return nextHelper;
    }

    public Optional<T> get() {
        return Optional.of(host);
    }
}
