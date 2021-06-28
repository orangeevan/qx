package com.haipaite.common.resource.support;

import com.haipaite.common.utility.JsonUtils;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;


public class JsonToArrayConverter implements ConditionalGenericConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType() != String.class) {
            return false;
        }
        if (!targetType.getType().isArray()) {
            return false;
        }
        return true;
    }

    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object[].class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String content = (String) source;
        if (targetType.getElementTypeDescriptor().getType().isPrimitive()) {
            return JsonUtils.string2Object(content, targetType.getObjectType());
        }
        return JsonUtils.string2List(content, targetType.getObjectType());
    }
}