package com.mmorpg.qx.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author zhang peng
 * @description:
 * @since 15:26 2021/3/10
 */
@Component
public class BeanService implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanService.applicationContext = applicationContext;
    }

    public static  <T> T getBean(String name) {
        if (applicationContext == null)
            return null;
        return (T)applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null)
            return null;
        return applicationContext.getBean(requiredType);
    }

    public static <T> Collection<T> getBeansOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type).values();
    }

}