package com.haipaite.common.resource.reader;

import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class ReaderHolder implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ReaderHolder.class);

    @PostConstruct
    protected void initialize() {
        for (String name : this.applicationContext.getBeanNamesForType(ResourceReader.class)) {
            ResourceReader reader = this.applicationContext.getBean(name, ResourceReader.class);
            register(reader);
        }
    }

    private ConcurrentHashMap<String, ResourceReader> readers = new ConcurrentHashMap<>();


    private ApplicationContext applicationContext;


    public ResourceReader getReader(String format) {
        return this.readers.get(format);
    }


    public ResourceReader register(ResourceReader reader) {
        return this.readers.putIfAbsent(reader.getFormat(), reader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}