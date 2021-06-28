 package com.haipaite.common.utility;
 
 import java.util.Date;
 import java.util.concurrent.Delayed;
 import java.util.concurrent.TimeUnit;
 
 
 
 
 
 
 public class DelayedElement<T>
   implements Delayed
 {
   private final T content;
   private final Date end;
   
   public static <T> DelayedElement<T> valueOf(T content, Date end) {
     return new DelayedElement<>(content, end);
   }
   
   public DelayedElement(T content, Date end) {
     this.content = content;
     this.end = end;
   }
   
   public T getContent() {
     return this.content;
   }
   
   public Date getEnd() {
     return this.end;
   }
 
   
   public long getDelay(TimeUnit timeUnit) {
     long now = System.currentTimeMillis();
     long delay = this.end.getTime() - now;
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
     return hashCode() - o.hashCode();
   }
 
 
   
   public int hashCode() {
     int prime = 31;
     int result = 1;
     result = 31 * result + ((this.content == null) ? 0 : this.content.hashCode());
     return result;
   }
 
 
   
   public boolean equals(Object obj) {
     if (this == obj)
       return true; 
     if (obj == null)
       return false; 
     if (getClass() != obj.getClass())
       return false; 
     DelayedElement other = (DelayedElement)obj;
     if (this.content == null) {
       if (other.content != null)
         return false; 
     } else if (!this.content.equals(other.content)) {
       return false;
     }  return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\DelayedElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */