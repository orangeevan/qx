package com.haipaite.common.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;


public abstract class ReflectionUtility extends ReflectionUtils {
    public static <A extends Annotation> Field findUniqueFieldWithAnnotation(Class<?> clz, final Class<A> type) {
        final List<Field> fields = new ArrayList<>();
        doWithFields(clz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fields.add(field);
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.isAnnotationPresent(type);
            }
        });

        if (fields.size() > 1) {
            throw new IllegalStateException("被注释" + type.getSimpleName() + "声明的域不唯一");
        }
        if (fields.size() == 1) {
            return fields.get(0);
        }
        return null;
    }


    public static void doWithDeclaredFields(Class<?> clazz, ReflectionUtils.FieldCallback fc, ReflectionUtils.FieldFilter ff) throws IllegalArgumentException {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (ff == null || ff.matches(field)) {
                try {
                    fc.doWith(field);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("非法访问属性 '" + field.getName() + "': " + ex);
                }
            }
        }
    }


    public static Field getFirstDeclaredFieldWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                return field;
            }
        }
        return null;
    }


    public static Field[] getDeclaredFieldsWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                fields.add(field);
            }
        }
        return fields.<Field>toArray(new Field[0]);
    }


    public static Method getFirstDeclaredMethodWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
    }


    public static Method[] getDeclaredMethodsWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                methods.add(method);
            }
        }
        return methods.<Method>toArray(new Method[0]);
    }


    public static Method[] getDeclaredGetMethodsWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clz.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) != null) {

                if (!method.getReturnType().equals(void.class)) {

                    if ((method.getParameterTypes()).length <= 0) {

                        methods.add(method);
                    }
                }
            }
        }
        return methods.<Method>toArray(new Method[0]);
    }
}