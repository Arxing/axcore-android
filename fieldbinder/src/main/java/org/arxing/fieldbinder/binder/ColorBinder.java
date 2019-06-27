package org.arxing.fieldbinder.binder;

import android.content.Context;
import android.support.annotation.ColorRes;

import org.arxing.fieldbinder.FieldBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColorBinder {
    @ColorRes int value();

    class Handler implements FieldBinder.Handler<ColorBinder> {

        @Override public Object bind(Context context, Field field, ColorBinder binder) {
            return context.getResources().getColor(binder.value());
        }
    }
}
