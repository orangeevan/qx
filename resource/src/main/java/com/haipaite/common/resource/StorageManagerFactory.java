package com.haipaite.common.resource;

import com.haipaite.common.resource.other.ResourceDefinition;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;


public class StorageManagerFactory implements FactoryBean<StorageManager>, ApplicationContextAware {
    private List<ResourceDefinition> definitions;
    private ApplicationContext applicationContext;

    public void setDefinitions(List<ResourceDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public StorageManager getObject() throws Exception {
        StorageManager result = this.applicationContext.getAutowireCapableBeanFactory().createBean(StorageManager.class);
        for (ResourceDefinition definition : this.definitions) {
            result.initialize(definition);
        }
        return result;
    }

    @Override
    public Class<StorageManager> getObjectType() {
        return StorageManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
