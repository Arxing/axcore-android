package org.arxing.fieldbinder;

import android.content.Context;

import com.annimon.stream.Stream;

import org.arxing.fieldbinder.binder.ColorBinder;
import org.arxing.fieldbinder.binder.NameBinder;
import org.arxing.fieldbinder.binder.RBinder;
import org.arxing.fieldbinder.binder.StringBinder;
import org.arxing.utils.AssertUtils;
import org.arxing.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FieldBinder {
    private final static Logger logger = new Logger(FieldBinder.class.getSimpleName());
    private static Map<Class<? extends Annotation>, Handler<? extends Annotation>> registered = new HashMap<>();

    static {
        registerBinder(StringBinder.class, new StringBinder.Handler());
        registerBinder(ColorBinder.class, new ColorBinder.Handler());
        registerBinder(NameBinder.class, new NameBinder.Handler());
        registerBinder(RBinder.class, new RBinder.Handler());
    }

    public static <TBinder extends Annotation> void registerBinder(Class<TBinder> binder, Handler<TBinder> handler) {
        registered.put(binder, handler);
    }

    public static <TBinder extends Annotation> void unregisterBinder(Class<TBinder> binder) {
        if (registered.containsKey(binder))
            registered.remove(binder);
    }

    public static <TBinder extends Annotation> Handler<TBinder> findHandler(Class<TBinder> binder) {
        if (registered.containsKey(binder))
            return (Handler<TBinder>) registered.get(binder);
        else
            AssertUtils.throwError("Unregister handler<%s>.", binder.getSimpleName());
        return null;
    }

    public static void bind(Context context, Class... classes) {
        Stream.of(classes)
              .flatMap(cls -> Stream.of(cls.getDeclaredFields()))
              .filter(f -> Modifier.isStatic(f.getModifiers()))
              .forEach(field -> Stream.of(field.getDeclaredAnnotations())
                                      .filter(anno -> registered.containsKey(anno.annotationType()))
                                      .forEach(anno -> {
                                          Handler handler = findHandler(anno.annotationType());
                                          Object value = handler.bind(context, field, anno);
                                          setField(field, value);
                                      }));
    }

    private static void setField(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(null, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public interface Handler<TBinder extends Annotation> {
        Object bind(Context context, Field field, TBinder binder);
    }

    public static <T> T newDeftValInstance(Class<T> cls) {
        return newDeftValInstance(cls, new DefaultValueAdapter());
    }

    /**
     * 實例一個物件且會將其可null的屬性替換成預設值
     */
    public static <T> T newDeftValInstance(Class<T> cls, ValueAdapter adapter) {
        try {
            T instance = cls.newInstance();
            Stream.of(cls.getDeclaredFields()).filter(field -> adapter.getRegisteredTypes().contains(field.getType())).forEach(field -> {
                Class type = field.getType();
                field.setAccessible(true);
                try {
                    Object defValue = adapter.getDefVal(field, instance, type);
                    field.set(instance, defValue);
                } catch (Exception e) {
                }
            });
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.e("Method newInstance(Class<T>) catch exception: %s", e.getMessage());
        }
        return null;
    }

    public static class DefaultValueAdapter implements ValueAdapter {

        @Override public Set<Class> getRegisteredTypes() {
            return new HashSet<>(Arrays.asList(String.class, Integer.class, Boolean.class, Long.class, Float.class, Double.class));
        }

        @Override public Object getDefVal(Field field, Object instance, Class type) throws IllegalAccessException {
            Object val = field.get(instance);
            if (val != null)
                return val;
            if (type.equals(String.class))
                return "";
            if (type.equals(Integer.class))
                return 0;
            if (type.equals(Boolean.class))
                return false;
            if (type.equals(Long.class))
                return 0L;
            if (type.equals(Float.class))
                return 0f;
            if (type.equals(Double.class))
                return 0d;
            return val;
        }
    }

    public interface ValueAdapter {

        /**
         * 註冊要覆寫的型態
         */
        Set<Class> getRegisteredTypes();

        /**
         * 覆寫此型態的值
         *
         * @param field    欄位
         * @param instance 實體
         * @param type     型態
         * @return 覆寫值
         */
        Object getDefVal(Field field, Object instance, Class type) throws IllegalAccessException;
    }
}
