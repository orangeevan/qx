 package com.haipaite.common.scheduler.impl;

 import com.haipaite.common.scheduler.ScheduledTask;
 import com.haipaite.common.scheduler.Trigger;
 import java.util.Date;
 import java.util.concurrent.Delayed;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;






 public class SchedulingRunner
   implements ScheduledTask, ScheduledFuture<Object>
 {
   private static final Logger log = LoggerFactory.getLogger(SchedulingRunner.class);


   private final ScheduledTask delegate;

   private final Trigger trigger;

   private final SimpleTaskContext taskContext = new SimpleTaskContext();

   private final FixScheduledThreadPoolExecutor executor;

   private volatile ScheduledFuture currentFuture;

   private volatile Date scheduledTime;

   private final Object mutex;


   public SchedulingRunner(ScheduledTask delegate, Trigger trigger, FixScheduledThreadPoolExecutor executor) {
     this.mutex = new Object();
     this.delegate = delegate;
     this.trigger = trigger;
     this.executor = executor; } public ScheduledFuture schedule() { synchronized (this.mutex) {
       this.scheduledTime = this.trigger.nextTime(this.taskContext);
       if (this.scheduledTime == null) {
         return null;
       }
       long initialDelay = this.scheduledTime.getTime() - System.currentTimeMillis();
       this.currentFuture = this.executor.schedule((Runnable)this, initialDelay, TimeUnit.MILLISECONDS);

       if (log.isDebugEnabled()) {
         log.debug("任务[{}]的下次计划执行时间[{}]", getName(), this.scheduledTime);
       }
       return this;
     }  }




   public void run() {
     Date actualExecutionTime = new Date();
     try {
       this.delegate.run();
     } catch (Exception e) {
       log.error("定时任务:{},执行异常", getName(), e);
     }
     Date completionTime = new Date();
     synchronized (this.mutex) {
       this.taskContext.update(this.scheduledTime, actualExecutionTime, completionTime);
     }

     if (!this.currentFuture.isCancelled()) {
       schedule();
     }
   }


   public String getName() {
     return this.delegate.getName();
   }



   public boolean cancel(boolean mayInterruptIfRunning) {
     return this.currentFuture.cancel(mayInterruptIfRunning);
   }

   public boolean isCancelled() {
     return this.currentFuture.isCancelled();
   }

   public boolean isDone() {
     return this.currentFuture.isDone();
   }

   public Object get() throws InterruptedException, ExecutionException {
     return this.currentFuture.get();
   }


   public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
     return this.currentFuture.get(timeout, unit);
   }

   public long getDelay(TimeUnit unit) {
     return this.currentFuture.getDelay(unit);
   }

   public int compareTo(Delayed other) {
     if (this == other) {
       return 0;
     }
     long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
     return (diff == 0L) ? 0 : ((diff < 0L) ? -1 : 1);
   }


   public boolean equals(Object obj) {
     return (this == obj);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\SchedulingRunner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */