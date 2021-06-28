package com.haipaite.common.resource.other;

import com.haipaite.common.resource.anno.Inject;
import com.haipaite.common.resource.anno.Resource;
import com.haipaite.common.utility.ReflectionUtility;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.util.ReflectionUtils;


public class ResourceDefinition {
    public static final String FILE_SPLIT = ".";
    public static final String FILE_PATH = File.separator;


    private static final ReflectionUtils.FieldFilter INJECT_FILTER = new ReflectionUtils.FieldFilter() {
        @Override
        public boolean matches(Field field) {
            if (field.isAnnotationPresent((Class) Inject.class)) {
                return true;
            }
            return false;
        }
    };


    private final Class<?> clz;

    private final String location;

    private final String format;

    private final Set<InjectDefinition> injects = new HashSet<>();

    private String cacheKey;

    public ResourceDefinition(Class<?> clz, FormatDefinition format) {
        this.clz = clz;
        this.format = format.getType();
        Resource anno = clz.<Resource>getAnnotation(Resource.class);
        if (StringUtils.isBlank(anno.value())) {
            String name = clz.getSimpleName();
            this.location = format.getLocation() + FILE_PATH + name + FILE_SPLIT + format.getSuffix();
        } else {
            String name = anno.value();
            if (StringUtils.startsWith(name, FILE_PATH)) {
                name = StringUtils.substringAfter(name, FILE_PATH);
            }
            this.location = format.getLocation() + FILE_PATH + name + FILE_SPLIT + format.getSuffix();
        }
        if (!StringUtils.isBlank(anno.cache())) {
            this.cacheKey = anno.cache();
        }
        ReflectionUtility.doWithDeclaredFields(clz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                InjectDefinition definition = new InjectDefinition(field);
                ResourceDefinition.this.injects.add(definition);
            }
        }, INJECT_FILTER);
    }


    public Set<InjectDefinition> getStaticInjects() {
        HashSet<InjectDefinition> result = new HashSet<>();
        for (InjectDefinition definition : this.injects) {
            Field field = definition.getField();
            if (Modifier.isStatic(field.getModifiers())) {
                result.add(definition);
            }
        }
        return result;
    }


    public Set<InjectDefinition> getInjects() {
        HashSet<InjectDefinition> result = new HashSet<>();
        for (InjectDefinition definition : this.injects) {
            Field field = definition.getField();
            if (!Modifier.isStatic(field.getModifiers())) {
                result.add(definition);
            }
        }
        return result;
    }


    public Class getClz() {
        return this.clz;
    }

    public String getLocation() {
        return this.location;
    }

    public String getFormat() {
        return this.format;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public String getCacheKey() {
        return this.cacheKey;
    }
}
