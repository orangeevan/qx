 package com.haipaite.common.event.util;
 
 import com.haipaite.common.event.core.EventBusManager;
 import org.springframework.context.support.ClassPathXmlApplicationContext;
 
 
 public class NamespaceTest
 {
   public static void main(String[] args) {
     ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("EventReceiverBeanPostProcessorTest-context.xml");
     context.start();
     for (int i = 0; i < 1000; i++) {
       EventBusManager.getInstance().submit(new TestEvent());
     }
     try {
       Thread.currentThread(); Thread.sleep(10000L);
     } catch (Exception exception) {}
 
     
     context.registerShutdownHook();
     context.stop();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\even\\util\NamespaceTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */