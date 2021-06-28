 package com.haipaite.common.scheduler.impl;

 import com.haipaite.common.scheduler.TaskContext;
 import com.haipaite.common.scheduler.Trigger;
 import java.util.Date;
 import java.util.TimeZone;




 public class CronTrigger
   implements Trigger
 {
   private final CronSequenceGenerator sequenceGenerator;

   public CronTrigger(String expression) {
     this(expression, TimeZone.getDefault());
   }

   public CronTrigger(String cronExpression, TimeZone timeZone) {
     this.sequenceGenerator = new CronSequenceGenerator(cronExpression, timeZone);
   }


   public Date nextTime(TaskContext context) {
     Date date = context.lastCompletionTime();
     if (date != null) {
       Date scheduled = context.lastScheduledTime();
       if (scheduled != null && date.before(scheduled)) {
         date = scheduled;
       }
     } else {
       date = new Date();
     }

     Date result = this.sequenceGenerator.next(date);
     return result;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\CronTrigger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */