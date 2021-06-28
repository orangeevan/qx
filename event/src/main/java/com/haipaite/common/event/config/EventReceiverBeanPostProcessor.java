 package com.haipaite.common.event.config;
 
 import com.haipaite.common.event.core.EventBusManager;
 import org.springframework.beans.BeansException;
 import org.springframework.beans.factory.config.BeanPostProcessor;
 import org.springframework.context.ApplicationContext;
 import org.springframework.context.ApplicationContextAware;
 import org.springframework.core.Ordered;
 
 public class EventReceiverBeanPostProcessor
   implements BeanPostProcessor, ApplicationContextAware, Ordered
 {
   private ApplicationContext applicationContext;
   
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
     try {
       getEventBusManager().registReceiver(bean);
     } catch (Exception e) {
       throw new RuntimeException(e);
     } 
     return bean;
   }
   
   private EventBusManager getEventBusManager() {
     return (EventBusManager)this.applicationContext.getBean(EventBusManager.class);
   }
 
   
   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
     return bean;
   }
 
   
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
     this.applicationContext = applicationContext;
   }
 
   
   public int getOrder() {
     return Integer.MAX_VALUE;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\config\EventReceiverBeanPostProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */