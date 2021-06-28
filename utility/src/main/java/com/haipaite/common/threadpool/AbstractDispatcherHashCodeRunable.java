 package com.haipaite.common.threadpool;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 
 
 public abstract class AbstractDispatcherHashCodeRunable
   implements Runnable
 {
   private static final Logger logger = LoggerFactory.getLogger(AbstractDispatcherHashCodeRunable.class);
   private static long TIME_OUT = 100000000L;
 
 
 
   
   private EventExecutor eventExecutor;
 
 
 
   
   public abstract int getDispatcherHashCode();
 
 
 
   
   public abstract String name();
 
 
 
   
   public long timeoutNanoTime() {
     return TIME_OUT;
   }
 
 
 
   
   public abstract void doRun();
 
 
 
   
   public final void run() {
     EventExecutor.RunableStatistics rs = this.eventExecutor.getRunableStatistics().get(name());
     long start = System.nanoTime();
     try {
       doRun();
     } catch (Throwable e) {
       logger.error("任务执行错误!", e);
     } finally {
       long cost = System.nanoTime() - start;
       if (cost >= timeoutNanoTime()) {
         rs.getTimeOutCount().incrementAndGet();
       }
       rs.getCurrentCount().decrementAndGet();
       rs.getExeTotalTime().addAndGet(cost);
     } 
   }
 
   
   public void setEventExecutor(EventExecutor eventExecutor) {
     this.eventExecutor = eventExecutor;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\common\threadpool\AbstractDispatcherHashCodeRunable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */