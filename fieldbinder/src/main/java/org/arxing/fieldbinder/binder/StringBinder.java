package org.arxing.fieldbinder.binder;

import android.content.Context;
import android.support.annotation.StringRes;

import org.arxing.fieldbinder.FieldBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringBinder {
    @StringRes int value();

    class Handler implements FieldBinder.Handler<StringBinder> {

        @Override public Object bind(Context context, Field field, StringBinder binder) {
            return context.getResources().getString(binder.value());
        }
    }
}
