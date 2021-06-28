 package com.haipaite.common.event.monitor;
 
 import com.haipaite.common.event.core.EventBusManager;
 import com.haipaite.common.event.event.IEvent;
 import com.haipaite.common.event.jmx.EventMBean;
 import com.haipaite.common.event.jmx.Stat;
 import java.lang.management.ManagementFactory;
 import javax.management.MBeanServer;
 import javax.management.ObjectName;
 
 
 public class MonitorEventBusManager
   extends EventBusManager
   implements EventMBean
 {
   private static final int maximumRuntimeInNanoWithoutWarning = 1000000;
   private static final Stat stat = new Stat();
 
   
   public MonitorEventBusManager() {
     regist();
   }
 
   
   private void regist() {
     try {
       MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
       ObjectName name = new ObjectName(this + ":type=EventMBean");
       mbs.registerMBean(this, name);
     } catch (Exception e) {
       e.printStackTrace();
     } 
   }
   
   protected void doSubmitEvent(IEvent event) {
     long start = System.nanoTime();
     super.doSubmitEvent(event);
     long use = System.nanoTime() - start;
     stat.addEvent(event.getClass(), use, (use > 1000000L));
   }
 
   
   public String[] getEventInfo() {
     return stat.getEventInfo();
   }
   
   public static Stat getStat() {
     return stat;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\monitor\MonitorEventBusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */