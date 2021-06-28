package com.haipaite.common.resource.other;

import com.haipaite.common.resource.anno.Inject;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;


public class InjectDefinition {
    private final Field field;
    private final Inject inject;
    private final InjectType type;

    public InjectDefinition(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("被注入属性域不能为null");
        }
        if (!field.isAnnotationPresent((Class) Inject.class)) {
            throw new IllegalArgumentException("被注入属性域" + field.getName() + "的注释配置缺失");
        }
        field.setAccessible(true);

        this.field = field;
        this.inject = field.<Inject>getAnnotation(Inject.class);
        if (StringUtils.isEmpty(this.inject.value())) {
            this.type = InjectType.CLASS;
        } else {
            this.type = InjectType.NAME;
        }
    }


    public Object getValue(ApplicationContext applicationContext) {
        if (InjectType.NAME.equals(this.type)) {
            return applicationContext.getBean(this.inject.value());
        }
        return applicationContext.getBean(this.field.getType());
    }


    public InjectType getType() {
        return this.type;
    }

    public Field getField() {
        return this.field;
    }

    public Inject getInject() {
        return this.inject;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\other\InjectDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */