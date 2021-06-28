 package com.haipaite.common.scheduler.impl;
 
 import com.haipaite.common.scheduler.Scheduled;
 import com.haipaite.common.scheduler.ScheduledTask;
 import com.haipaite.common.scheduler.Scheduler;
 import com.haipaite.common.scheduler.ValueType;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Map;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.aop.support.AopUtils;
 import org.springframework.beans.BeansException;
 import org.springframework.beans.factory.BeanFactory;
 import org.springframework.beans.factory.BeanFactoryAware;
 import org.springframework.beans.factory.NoSuchBeanDefinitionException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.config.BeanPostProcessor;
 import org.springframework.context.ApplicationEvent;
 import org.springframework.context.ApplicationListener;
 import org.springframework.context.event.ContextRefreshedEvent;
 import org.springframework.context.expression.BeanFactoryResolver;
 import org.springframework.core.Ordered;
 import org.springframework.core.annotation.AnnotationUtils;
 import org.springframework.expression.BeanResolver;
 import org.springframework.expression.EvaluationContext;
 import org.springframework.expression.spel.standard.SpelExpressionParser;
 import org.springframework.expression.spel.support.StandardEvaluationContext;
 import org.springframework.scheduling.support.MethodInvokingRunnable;
 import org.springframework.stereotype.Component;
 import org.springframework.util.ReflectionUtils;
 
 
 
 
 
 
 
 @Component
 public class ScheduledProcessor
   implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, Ordered, BeanFactoryAware
 {
   private static final Logger logger = LoggerFactory.getLogger(ScheduledProcessor.class);
 
   
   private final Map<ScheduledTask, String> tasks = new HashMap<>(); @Autowired
   private Scheduler scheduler;
   
   public Object postProcessAfterInitialization(final Object bean, String beanName) {
     ReflectionUtils.doWithMethods(AopUtils.getTargetClass(bean), new ReflectionUtils.MethodCallback() {
           public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
             Scheduled annotation = (Scheduled)AnnotationUtils.getAnnotation(method, Scheduled.class);
             if (annotation == null) {
               return;
             }
             
             ScheduledTask task = ScheduledProcessor.this.createTask(bean, method, annotation, new Object[0]);
             String experssion = ScheduledProcessor.this.resolveExperssion(bean, annotation);
             ScheduledProcessor.this.tasks.put(task, experssion);
           }
         });
     return bean;
   }
   private BeanFactory beanFactory;
   public void selfCreateTak(Object bean, Method method, Scheduled annotation, Object... parameter) {
     ScheduledTask task = createTask(bean, method, annotation, parameter);
     String experssion = resolveExperssion(bean, annotation);
     this.tasks.put(task, experssion);
   }
 
   
   private ScheduledTask createTask(Object bean, Method method, Scheduled annotation, Object... parameter) {
     if (!void.class.equals(method.getReturnType())) {
       throw new IllegalArgumentException("定时方法的返回值必须为 void");
     }
 
 
 
 
     
     final MethodInvokingRunnable runnable = new MethodInvokingRunnable();
     runnable.setTargetObject(bean);
     runnable.setTargetMethod(method.getName());
     runnable.setArguments((parameter == null) ? new Object[0] : parameter);
     try {
       runnable.prepare();
     } catch (Exception e) {
       throw new IllegalStateException("无法创建定时任务", e);
     } 
     
     final String name = annotation.name();
     return new ScheduledTask()
       {
         public void run() {
           runnable.run();
         }
 
         
         public String getName() {
           return name;
         }
       };
   }
 
   
   private String resolveExperssion(Object bean, Scheduled annotation) {
     String name, result = null;
     switch (annotation.type()) {
       case EXPRESSION:
         result = annotation.value();
         break;
       case BEANNAME:
         name = annotation.value();
         try {
           Object obj = this.beanFactory.getBean(name);
           if (obj instanceof String) {
             result = (String)obj; break;
           } 
           result = annotation.defaultValue();
         }
         catch (NoSuchBeanDefinitionException e) {
           logger.error("无法获取定时任务配置[{}],将使用默认值替代", annotation);
           result = annotation.defaultValue();
         } 
         break;
       case SPEL:
         try {
           SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
           StandardEvaluationContext context = new StandardEvaluationContext();
           context.setBeanResolver((BeanResolver)new BeanFactoryResolver(this.beanFactory));
           result = (String)spelExpressionParser.parseExpression(annotation.value()).getValue((EvaluationContext)context, String.class);
         } catch (Exception e) {
           logger.error("无法获取定时任务配置[{}],将使用默认值替代", annotation);
           result = annotation.defaultValue();
         } 
         break;
     } 
 
     
     return result;
   }
 
 
 
 
 
   
   public void onApplicationEvent(ContextRefreshedEvent event) {
     for (Map.Entry<ScheduledTask, String> entry : this.tasks.entrySet()) {
       this.scheduler.schedule(entry.getKey(), new CronTrigger(entry.getValue()));
     }
   }
   
   public int getOrder() {
     return Integer.MAX_VALUE;
   }
   
   public Object postProcessBeforeInitialization(Object bean, String beanName) {
     return bean;
   }
 
 
 
   
   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
     this.beanFactory = beanFactory;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\ScheduledProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */