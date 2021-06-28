package com.haipaite.common.ramcache.schema;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.ServiceManager;
import com.haipaite.common.ramcache.orm.Accessor;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.persist.PersisterConfig;

import java.util.Map;
import java.util.Set;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.FactoryBean;


public class ServiceManagerFactory implements FactoryBean<ServiceManager> {
    public static final String ENTITY_CLASSES_NAME = "entityClasses";
    public static final String PERSISTER_CONFIG_NAME = "persisterConfig";
    private Accessor accessor;
    private Querier querier;
    private Set<Class<IEntity>> entityClasses;
    private Map<String, PersisterConfig> persisterConfig;
    private Map<String, Integer> constants;
    private ServiceManager cacheServiceManager;

    @Override
    public ServiceManager getObject() throws Exception {
        this.cacheServiceManager = new ServiceManager(this.entityClasses, this.accessor, this.querier, this.constants, this.persisterConfig);
        return this.cacheServiceManager;
    }

    @PreDestroy
    public void shutdown() {
        if (this.cacheServiceManager == null) {
            return;
        }
        this.cacheServiceManager.shutdown();
    }


    public void setAccessor(Accessor accessor) {
        this.accessor = accessor;
    }


    public void setQuerier(Querier querier) {
        this.querier = querier;
    }

    public void setEntityClasses(Set<Class<IEntity>> entityClasses) {
        this.entityClasses = entityClasses;
    }

    public void setPersisterConfig(Map<String, PersisterConfig> persisterConfig) {
        this.persisterConfig = persisterConfig;
    }

    public void setConstants(Map<String, Integer> constants) {
        this.constants = constants;
    }

    @Override
    public Class<?> getObjectType() {
        return ServiceManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}