 package com.haipaite.common.threadpool;
 
 import com.haipaite.common.utility.JsonUtils;
 import com.haipaite.common.utility.ThreadUtils;
 import io.netty.util.concurrent.DefaultThreadFactory;
 import io.netty.util.concurrent.Future;
 import io.netty.util.concurrent.ScheduledFuture;
 import java.util.Arrays;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class IdentityEventExecutorGroup
 {
   private static EventExecutor[] children;
   
   public static synchronized void init(int nThreads) {
     if (children == null) {
       children = new EventExecutor[nThreads];
       for (int i = 0; i < nThreads; i++) {
         children[i] = new EventExecutor(null, (ThreadFactory)new DefaultThreadFactory("Identity-dispatcher"), true);
       }
     } 
   }
 
 
 
 
 
 
   
   public static Future<?> addTask(AbstractDispatcherHashCodeRunable dispatcherHashCode) {
     EventExecutor eventExecutor = children[dispatcherHashCode.getDispatcherHashCode() % children.length];
     dispatcherHashCode.setEventExecutor(eventExecutor);
     return eventExecutor.addTask(dispatcherHashCode);
   }
 
 
 
 
 
 
 
 
 
   
   public static ScheduledFuture<?> addScheduleTask(AbstractDispatcherHashCodeRunable dispatcherHashCode, long delay, TimeUnit unit) {
     EventExecutor eventExecutor = children[dispatcherHashCode.getDispatcherHashCode() % children.length];
     dispatcherHashCode.setEventExecutor(eventExecutor);
     return eventExecutor.addScheduleTask(dispatcherHashCode, delay, unit);
   }
 
 
 
 
 
 
 
 
 
 
   
   public static ScheduledFuture<?> addScheduleAtFixedRate(AbstractDispatcherHashCodeRunable dispatcherHashCode, long initialDelay, long period, TimeUnit unit) {
     EventExecutor eventExecutor = children[dispatcherHashCode.getDispatcherHashCode() % children.length];
     dispatcherHashCode.setEventExecutor(eventExecutor);
     return eventExecutor.addScheduleAtFixedRate(dispatcherHashCode, initialDelay, period, unit);
   }
   
   public static void printRunableStatistics() {
     for (EventExecutor c : children) {
       String stat = JsonUtils.map2String(c.getRunableStatistics());
       System.out.println(stat);
     } 
   }
   
   public static void shutdown() {
     if (children == null) {
       return;
     }
     Arrays.<EventExecutor>stream(children).forEach(eventExecutors -> {
           try {
             ThreadUtils.shutdownGracefully((ExecutorService)eventExecutors, 1000L, TimeUnit.MILLISECONDS);
           } catch (Exception e) {
             e.printStackTrace();
           } finally {
             System.err.println("IdentityEventExecutorGroup 业务线程:" + eventExecutors.toString() + " 已经关闭");
           } 
         });
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\common\threadpool\IdentityEventExecutorGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */