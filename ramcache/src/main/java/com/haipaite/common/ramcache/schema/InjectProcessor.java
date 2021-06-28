package com.haipaite.common.ramcache.schema;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.ServiceManager;
import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.ramcache.service.RegionCacheService;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;


public class InjectProcessor extends InstantiationAwareBeanPostProcessorAdapter implements Ordered {
    private static final Logger logger = LoggerFactory.getLogger(InjectProcessor.class);

    @Autowired
    private ServiceManager manager;

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Inject anno = field.<Inject>getAnnotation(Inject.class);
                if (anno == null) {
                    return;
                }
                if (field.getType().equals(EntityCacheService.class)) {
                    InjectProcessor.this.injectEntityCacheService(bean, beanName, field);
                } else if (field.getType().equals(RegionCacheService.class)) {
                    InjectProcessor.this.injectRegionCacheService(bean, beanName, field);
                } else {
                    FormattingTuple message = MessageFormatter.format("Bean[]的注入属性[]类型声明错误", beanName, field.getName());
                    InjectProcessor.logger.error(message.getMessage());
                    throw new ConfigurationException(message.getMessage());
                }
            }
        });
        return super.postProcessAfterInstantiation(bean, beanName);
    }


    private void injectEntityCacheService(Object bean, String beanName, Field field) {
        Class<? extends IEntity> clz = null;
        EntityCacheService service = null;
        try {
            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            clz = (Class<? extends IEntity>) types[1];
            service = this.manager.getEntityService(clz);
            if (Objects.isNull(service)) {
                service = this.manager.createEntityService(clz);
            }
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("Bean[{}]的注入属性[{}]类型声明错误", beanName, field.getName());
            logger.error(message.getMessage());
            throw new ConfigurationException(message.getMessage());
        }
        if (service == null) {
            FormattingTuple message = MessageFormatter.format("实体[{}]缓存服务对象不存在", clz.getName());
            logger.debug(message.getMessage());
            throw new ConfigurationException(message.getMessage());
        }
        inject(bean, field, service);
    }


    private void injectRegionCacheService(Object bean, String beanName, Field field) {
        Class<? extends IEntity> clz = null;
        RegionCacheService service = null;
        try {
            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            clz = (Class<? extends IEntity>) types[1];
            service = this.manager.getRegionService(clz);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("Bean[{}]的注入属性[{}]类型声明错误", beanName, field.getName());
            logger.error(message.getMessage());
            throw new ConfigurationException(message.getMessage());
        }
        if (service == null) {
            FormattingTuple message = MessageFormatter.format("实体[{}]缓存服务对象不存在", clz.getName());
            logger.debug(message.getMessage());
            throw new ConfigurationException(message.getMessage());
        }
        inject(bean, field, service);
    }


    private void inject(Object bean, Field field, Object value) {
        ReflectionUtils.makeAccessible(field);
        try {
            field.set(bean, value);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("属性[{}]注入失败", field);
            logger.debug(message.getMessage());
            throw new ConfigurationException(message.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}