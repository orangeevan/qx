 package com.haipaite.common.scheduler.impl;

 import com.haipaite.common.scheduler.ScheduledTask;
 import com.haipaite.common.scheduler.Scheduler;
 import com.haipaite.common.scheduler.Trigger;
 import com.haipaite.common.utility.thread.NamedThreadFactory;
 import java.util.Date;
 import java.util.concurrent.RejectedExecutionException;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import javax.annotation.PostConstruct;
 import javax.annotation.PreDestroy;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Qualifier;
 import org.springframework.stereotype.Component;







 @Component
 public class SimpleScheduler
   implements Scheduler
 {
   private static final Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);

   @Autowired(required = false)
   @Qualifier("scheduling_delay_time")
   private Long delayTime = Long.valueOf(60000L);
   @Autowired(required = false)
   @Qualifier("scheduling_pool_size")
   private Integer poolSize = Integer.valueOf(5);

   private FixScheduledThreadPoolExecutor executor;

   @PostConstruct
   protected void init() {
     if (logger.isInfoEnabled()) {
       logger.info("定时任务线程池大小:{}，修正时间延迟:{}", this.poolSize, this.delayTime);
     }
     ThreadGroup group = new ThreadGroup("定时任务");
     NamedThreadFactory threadFactory = new NamedThreadFactory(group, "处理");
     this.executor = new FixScheduledThreadPoolExecutor(this.poolSize, this.delayTime, (ThreadFactory)threadFactory);
   }

   @PreDestroy
   protected void destory() {
     if (this.executor != null) {
       this.executor.shutdownNow();
     }
     if (logger.isDebugEnabled()) {
       logger.debug("定时任务调度器已经关闭");
     }
   }




   public int getSchedulerQueueSize() {
     return this.executor.getQueue().size();
   }




   public int getPoolActiveCount() {
     return this.executor.getActiveCount();
   }


   public ScheduledFuture schedule(ScheduledTask task, Trigger trigger) {
     try {
       task = new LogDecorateTask(task);
       return (new SchedulingRunner(task, trigger, this.executor)).schedule();
     } catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }


   public ScheduledFuture schedule(ScheduledTask task, Date startTime) {
     long initialDelay = startTime.getTime() - System.currentTimeMillis();
     try {
       task = new LogDecorateTask(task);
       return this.executor.schedule((Runnable)task, initialDelay, TimeUnit.MILLISECONDS);
     } catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }


   public ScheduledFuture<?> schedule(ScheduledTask task, String cron) {
     CronTrigger trigger = new CronTrigger(cron);
     return schedule(task, trigger);
   }


   public ScheduledFuture scheduleAtFixedRate(ScheduledTask task, Date startTime, long period) {
     long initialDelay = startTime.getTime() - System.currentTimeMillis();
     try {
       task = new LogDecorateTask(task);
       return this.executor.scheduleAtFixedRate((Runnable)task, initialDelay, period, TimeUnit.MILLISECONDS);
     }
     catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }


   public ScheduledFuture scheduleAtFixedRate(ScheduledTask task, long period) {
     try {
       task = new LogDecorateTask(task);
       return this.executor.scheduleAtFixedRate((Runnable)task, 0L, period, TimeUnit.MILLISECONDS);
     } catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }


   public ScheduledFuture scheduleWithFixedDelay(ScheduledTask task, Date startTime, long delay) {
     long initialDelay = startTime.getTime() - System.currentTimeMillis();
     try {
       task = new LogDecorateTask(task);
       return this.executor.scheduleWithFixedDelay((Runnable)task, initialDelay, delay, TimeUnit.MILLISECONDS);
     }
     catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }


   public ScheduledFuture scheduleWithDelay(ScheduledTask task, long delay) {
     try {
       task = new LogDecorateTask(task);
       return this.executor.schedule((Runnable)task, delay, TimeUnit.MILLISECONDS);
     } catch (RejectedExecutionException ex) {
       throw new TaskRejectedException("执行器不接受[" + task.getName() + "]该任务", ex);
     }
   }



   private static class LogDecorateTask
     implements ScheduledTask
   {
     private ScheduledTask task;


     public LogDecorateTask(ScheduledTask task) {
       this.task = task;
     }

     public String getName() {
       return this.task.getName();
     }

     public void run() {
       if (SimpleScheduler.logger.isInfoEnabled()) {
         SimpleScheduler.logger.info("任务[{}]开始运行时间[{}]", this.task.getName(), new Date());
       }
       try {
         this.task.run();
       } catch (RuntimeException e) {
         SimpleScheduler.logger.error("任务[" + this.task.getName() + "]在执行时出现异常!", e);
         throw e;
       }
       if (SimpleScheduler.logger.isInfoEnabled())
         SimpleScheduler.logger.info("任务[{}]结束运行时间[{}]", this.task.getName(), new Date());
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\SimpleScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */