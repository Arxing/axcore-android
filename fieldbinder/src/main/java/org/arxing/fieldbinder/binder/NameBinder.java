package org.arxing.fieldbinder.binder;

import android.content.Context;

import org.arxing.fieldbinder.FieldBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NameBinder {

    String value() default "";

    String prefix() default "";

    String suffix() default "";

    class Handler implements FieldBinder.Handler<NameBinder> {

        @Override public Object bind(Context context, Field field, NameBinder binder) {
            String name = binder.value().isEmpty() ? field.getName() : binder.value();
            return binder.prefix() + name + binder.suffix();
        }
    }
}
