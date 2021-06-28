 package com.haipaite.common.scheduler.impl;
 
 import com.haipaite.common.scheduler.TaskContext;
 import java.util.Date;
 
 
 
 
 
 
 
 public class SimpleTaskContext
   implements TaskContext
 {
   private volatile Date lastScheduledTime;
   private volatile Date lastActualTime;
   private volatile Date lastCompletionTime;
   
   public void update(Date scheduledTime, Date actualTime, Date completionTime) {
     this.lastScheduledTime = scheduledTime;
     this.lastActualTime = actualTime;
     this.lastCompletionTime = completionTime;
   }
   
   public Date lastScheduledTime() {
     return this.lastScheduledTime;
   }
   
   public Date lastActualTime() {
     return this.lastActualTime;
   }
   
   public Date lastCompletionTime() {
     return this.lastCompletionTime;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\SimpleTaskContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */