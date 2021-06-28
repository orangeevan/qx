 package com.haipaite.common.scheduler.impl;

 import java.lang.management.ManagementFactory;
 import javax.annotation.PostConstruct;
 import javax.management.MBeanServer;
 import javax.management.ObjectName;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;


 @Service
 public class SchedulerMonitor
   implements SchedulerMonitorMBean
 {
   private final Logger logger = LoggerFactory.getLogger(getClass());

   @Autowired
   private SimpleScheduler scheduler;


   @PostConstruct
   protected void init() {
     try {
       MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
       ObjectName name = new ObjectName("com.my9yu.common:type=SchedulerMBean");
       mbs.registerMBean(this, name);
     } catch (Exception e) {
       this.logger.error("JMX", e);
     }
   }


   public int getSchedulerQueueSize() {
     return this.scheduler.getSchedulerQueueSize();
   }


   public int getPoolActiveCount() {
     return this.scheduler.getPoolActiveCount();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\SchedulerMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */