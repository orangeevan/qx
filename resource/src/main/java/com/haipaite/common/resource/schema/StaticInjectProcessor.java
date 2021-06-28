package com.haipaite.common.resource.schema;

import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.StorageManager;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.ReflectionUtility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;


public class StaticInjectProcessor extends InstantiationAwareBeanPostProcessorAdapter implements Ordered {
    private static final Logger logger = LoggerFactory.getLogger(StaticInjectProcessor.class);

    @Autowired
    private StorageManager manager;
    @Autowired
    private ConversionService conversionService;

    public enum InjectType {
        STORAGE,

        INSTANCE;
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Static anno = field.<Static>getAnnotation(Static.class);
                if (anno == null) {
                    return;
                }
                StaticInjectProcessor.InjectType type = StaticInjectProcessor.this.checkInjectType(field);
                switch (type) {
                    case STORAGE:
                        StaticInjectProcessor.this.injectStorage(bean, field, anno);
                        break;
                    case INSTANCE:
                        StaticInjectProcessor.this.injectInstance(bean, field, anno);
                        break;
                }
            }
        });
        return super.postProcessAfterInstantiation(bean, beanName);
    }


    private void injectInstance(Object bean, Field field, Static anno) {
        Class<?> clz = getIdType(field.getType());
        Object key = this.conversionService.convert(anno.value(), clz);

        Storage storage = this.manager.getStorage(field.getType());
        StaticObserver observer = new StaticObserver(bean, field, anno, key);
        storage.addObserver(observer);


        Object instance = storage.get(key, false);
        if (anno.required() && instance == null) {
            FormattingTuple message = MessageFormatter.format("属性[{}]的注入值不存在", field);
            logger.debug(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
        inject(bean, field, instance);
    }


    private Class<?> getIdType(Class<?> clz) {
        Field field = ReflectionUtility.getFirstDeclaredFieldWith(clz, Id.class);
        return field.getType();
    }


    private void injectStorage(Object bean, Field field, Static anno) {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            String message = "类型声明不正确";
            logger.debug(message);
            throw new RuntimeException(message);
        }

        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (!(types[1] instanceof Class)) {
            String message = "类型声明不正确";
            logger.debug(message);
            throw new RuntimeException(message);
        }

        Class clz = (Class) types[1];
        Storage storage = this.manager.getStorage(clz);

        boolean required = anno.required();
        if (required && storage == null) {
            FormattingTuple message = MessageFormatter.format("静态资源类[{}]不存在", clz);
            logger.debug(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }

        inject(bean, field, storage);
        if (Observer.class.isAssignableFrom(bean.getClass())) {
            storage.addObserver((Observer) bean);
        }
    }


    private void inject(Object bean, Field field, Object value) {
        ReflectionUtils.makeAccessible(field);
        try {
            field.set(bean, value);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("属性[{}]注入失败", field);
            logger.debug(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
    }


    private InjectType checkInjectType(Field field) {
        if (field.getType().equals(Storage.class)) {
            return InjectType.STORAGE;
        }
        return InjectType.INSTANCE;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}