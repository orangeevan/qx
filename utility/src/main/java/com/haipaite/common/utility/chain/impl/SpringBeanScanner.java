 package com.haipaite.common.utility.chain.impl;
 
 import com.haipaite.common.utility.chain.ChainBuilder;
 import com.haipaite.common.utility.chain.Dispatcher;
 import com.haipaite.common.utility.chain.NodeProcessor;
 import com.haipaite.common.utility.chain.ProcessChain;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Map;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.aop.support.AopUtils;
 import org.springframework.beans.BeansException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.config.BeanPostProcessor;
 import org.springframework.context.ApplicationEvent;
 import org.springframework.context.ApplicationListener;
 import org.springframework.context.event.ContextRefreshedEvent;
 import org.springframework.stereotype.Component;
 import org.springframework.util.ReflectionUtils;
 
 
 
 
 
 
 
 
 
 
 @Component
 public class SpringBeanScanner
   implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>
 {
   private static final Logger log = LoggerFactory.getLogger(SpringBeanScanner.class);
 
   
   private final Map<String, ChainBuilder> builders = new HashMap<>();
 
   
   @Autowired
   private Dispatcher processor;
 
 
   
   public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
     ReflectionUtils.doWithMethods(AopUtils.getTargetClass(bean), new ReflectionUtils.MethodCallback() {
           public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
             if (!AnnotationMethodNodeBuilder.isValid(method)) {
               return;
             }
             SpringBeanScanner.this.addNodeProcessor(bean, method);
           }
         });
     return bean;
   }
 
 
 
 
 
 
   
   private void addNodeProcessor(Object bean, Method method) {
     NodeProcessor processor = AnnotationMethodNodeBuilder.build(bean, method);
     String name = processor.getName();
     if (!this.builders.containsKey(name)) {
       this.builders.put(name, new ChainBuilder());
     }
     ChainBuilder builder = this.builders.get(name);
     builder.addNode(processor);
   }
 
 
   
   public void onApplicationEvent(ContextRefreshedEvent event) {
     for (Map.Entry<String, ChainBuilder> entry : this.builders.entrySet()) {
       String name = entry.getKey();
       ChainBuilder builder = entry.getValue();
       ProcessChain chain = builder.build();
       chain = this.processor.register(name, chain);
       if (chain != null) {
         log.error("处理链[{}]重复", name);
       }
     } 
   }
 
 
 
   
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
     return bean;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\impl\SpringBeanScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */