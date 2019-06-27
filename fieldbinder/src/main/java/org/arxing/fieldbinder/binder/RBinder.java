package org.arxing.fieldbinder.binder;

import android.content.Context;

import org.arxing.fieldbinder.FieldBinder;
import org.arxing.utils.AssertUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RBinder {
    String name();

    RType type();

    class Handler implements FieldBinder.Handler<RBinder> {

        @Override public Object bind(Context context, Field field, RBinder binder) {
            int id = context.getResources().getIdentifier(binder.name(), binder.type().name(), context.getPackageName());
            switch (binder.type()) {
                case string:
                    return context.getResources().getString(id);
                case color:
                    return context.getResources().getColor(id);
            }
            AssertUtils.throwError("unhandled type: %s", binder.type());
            return null;
        }
    }

    enum RType {
        string,
        color
    }
}
