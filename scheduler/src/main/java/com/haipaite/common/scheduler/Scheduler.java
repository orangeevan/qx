package com.haipaite.common.scheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public interface Scheduler {
  ScheduledFuture<?> schedule(ScheduledTask paramScheduledTask, Trigger paramTrigger);
  
  ScheduledFuture<?> schedule(ScheduledTask paramScheduledTask, Date paramDate);
  
  ScheduledFuture<?> schedule(ScheduledTask paramScheduledTask, String paramString);
  
  ScheduledFuture<?> scheduleAtFixedRate(ScheduledTask paramScheduledTask, Date paramDate, long paramLong);
  
  ScheduledFuture<?> scheduleAtFixedRate(ScheduledTask paramScheduledTask, long paramLong);
  
  ScheduledFuture<?> scheduleWithFixedDelay(ScheduledTask paramScheduledTask, Date paramDate, long paramLong);
  
  ScheduledFuture<?> scheduleWithDelay(ScheduledTask paramScheduledTask, long paramLong);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\Scheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */