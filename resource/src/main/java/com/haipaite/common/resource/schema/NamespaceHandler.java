package com.haipaite.common.resource.schema;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class NamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("config", (BeanDefinitionParser) new ConfigDefinitionParser());
    }
}
