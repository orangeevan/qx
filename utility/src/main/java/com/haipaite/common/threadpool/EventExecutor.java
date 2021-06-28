 package com.haipaite.common.threadpool;
 
 import io.netty.util.concurrent.EventExecutorGroup;
 import io.netty.util.concurrent.Future;
 import io.netty.util.concurrent.ScheduledFuture;
 import io.netty.util.concurrent.SingleThreadEventExecutor;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicLong;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 
 
 class EventExecutor
   extends SingleThreadEventExecutor
 {
   private static final Logger logger = LoggerFactory.getLogger(EventExecutor.class);
 
 
 
   
   private ConcurrentHashMap<String, RunableStatistics> runableStatistics = new ConcurrentHashMap<>();
 
 
 
   
   static class RunableStatistics
   {
     private String name;
 
 
     
     private AtomicLong currentCount = new AtomicLong(0L);
     private AtomicLong timeOutCount = new AtomicLong(0L);
     private AtomicLong historyCount = new AtomicLong(0L);
     private AtomicLong exeTotalTime = new AtomicLong(0L);
     
     public static RunableStatistics valueOf(String name) {
       RunableStatistics rs = new RunableStatistics();
       rs.name = name;
       return rs;
     }
     
     public String getName() {
       return this.name;
     }
     
     public void setName(String name) {
       this.name = name;
     }
     
     public AtomicLong getCurrentCount() {
       return this.currentCount;
     }
     
     public void setCurrentCount(AtomicLong currentCount) {
       this.currentCount = currentCount;
     }
     
     public AtomicLong getTimeOutCount() {
       return this.timeOutCount;
     }
     
     public void setTimeOutCount(AtomicLong timeOutCount) {
       this.timeOutCount = timeOutCount;
     }
     
     public AtomicLong getHistoryCount() {
       return this.historyCount;
     }
     
     public void setHistoryCount(AtomicLong historyCount) {
       this.historyCount = historyCount;
     }
     
     public AtomicLong getExeTotalTime() {
       return this.exeTotalTime;
     }
     
     public void setExeTotalTime(AtomicLong exeTotalTime) {
       this.exeTotalTime = exeTotalTime;
     }
   }
 
   
   protected EventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
     super(parent, threadFactory, addTaskWakesUp);
   }
 
   
   protected void run() {
     do {
       Runnable task = takeTask();
       if (task == null)
         continue;  task.run();
       updateLastExecutionTime();
     }
     while (!confirmShutdown());
   }
 
 
 
   
   public ScheduledFuture<?> addScheduleTask(AbstractDispatcherHashCodeRunable task, long delay, TimeUnit unit) {
     statistics(task);
     return schedule(task, delay, unit);
   }
 
   
   public ScheduledFuture<?> addScheduleAtFixedRate(final AbstractDispatcherHashCodeRunable task, long initialDelay, long period, TimeUnit unit) {
     return scheduleAtFixedRate(new Runnable()
         {
           public void run()
           {
             EventExecutor.this.addTask(task);
           }
         },  initialDelay, period, unit);
   }
 
 
 
 
 
 
   
   private void statistics(AbstractDispatcherHashCodeRunable task) {
     synchronized (this.runableStatistics) {
       if (!this.runableStatistics.containsKey(task.name())) {
         this.runableStatistics.put(task.name(), RunableStatistics.valueOf(task.name()));
       }
     } 
     RunableStatistics rs = this.runableStatistics.get(task.name());
     rs.getCurrentCount().incrementAndGet();
     rs.getHistoryCount().incrementAndGet();
   }
   
   public Future<?> addTask(AbstractDispatcherHashCodeRunable task) {
     statistics(task);
     return submit(task);
   }
   
   public ConcurrentHashMap<String, RunableStatistics> getRunableStatistics() {
     return this.runableStatistics;
   }
   
   public void setRunableStatistics(ConcurrentHashMap<String, RunableStatistics> runableStatistics) {
     this.runableStatistics = runableStatistics;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\common\threadpool\EventExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */