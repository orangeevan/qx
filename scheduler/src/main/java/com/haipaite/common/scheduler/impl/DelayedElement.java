 package com.haipaite.common.scheduler.impl;
 
 import java.util.Date;
 import java.util.concurrent.Delayed;
 import java.util.concurrent.TimeUnit;
 
 
 
 
 
 
 
 
 
 
 
 public class DelayedElement
   implements Delayed
 {
   private Date start;
   private long len;
   
   public Date getStart() {
     return this.start;
   }
 
 
 
   
   public long getLen() {
     return this.len;
   }
 
 
 
 
   
   public void addLen(long add) {
     this.len += add;
   }
 
 
 
   
   protected void setStart(Date start) {
     this.start = start;
   }
 
   
   protected void setLen(long len) {
     this.len = len;
   }
 
   
   public long getDelay(TimeUnit timeUnit) {
     long now = System.currentTimeMillis();
     long millis = (this.start == null) ? 0L : this.start.getTime();
     long delay = millis + this.len * 1000L - now;
     switch (timeUnit) {
       case MILLISECONDS:
         return delay;
       case SECONDS:
         return TimeUnit.MILLISECONDS.toSeconds(delay);
       case MINUTES:
         return TimeUnit.MILLISECONDS.toMinutes(delay);
       case HOURS:
         return TimeUnit.MILLISECONDS.toHours(delay);
       case DAYS:
         return TimeUnit.MILLISECONDS.toDays(delay);
       case MICROSECONDS:
         return TimeUnit.MILLISECONDS.toMicros(delay);
       case NANOSECONDS:
         return TimeUnit.MILLISECONDS.toNanos(delay);
     } 
     return delay;
   }
 
   
   public int compareTo(Delayed o) {
     long delay1 = getDelay(TimeUnit.MILLISECONDS);
     long delay2 = o.getDelay(TimeUnit.MILLISECONDS);
     int result = Long.valueOf(delay1).compareTo(Long.valueOf(delay2));
     if (result != 0) {
       return result;
     }
     if (equals(o)) {
       return 0;
     }
     if (hashCode() < o.hashCode()) {
       return -1;
     }
     return 1;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\DelayedElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */