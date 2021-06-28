 package com.haipaite.common.ramcache.util;
 
 import org.springframework.context.support.ClassPathXmlApplicationContext;
 
 
 
 
 
 
 
 
 
 public class JDBCTest
 {
   public static void main(String[] args) {
     ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext_randomcache.xml");
     System.err.println("系统开始运行");
     applicationContext.start();
     
     JDBCOp op = (JDBCOp)applicationContext.getBean(JDBCOp.class);
     op.operate();
     try {
       Thread.currentThread(); Thread.sleep(100000L);
     } catch (Exception exception) {}
 
 
     
     applicationContext.stop();
     System.err.println("系统结束");
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcach\\util\JDBCTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */