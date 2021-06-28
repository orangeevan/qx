package com.haipaite.common.event.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class EventNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("config", new EventBeanDefinitionParser());
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\config\EventNamespaceHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */