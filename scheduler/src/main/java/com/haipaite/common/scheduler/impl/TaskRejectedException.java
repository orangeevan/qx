 package com.haipaite.common.scheduler.impl;
 
 
 
 
 
 
 
 
 
 
 
 
 public class TaskRejectedException
   extends RuntimeException
 {
   private static final long serialVersionUID = 6681519082476492615L;
   
   public TaskRejectedException() {}
   
   public TaskRejectedException(String message, Throwable cause) {
     super(message, cause);
   }
 
 
 
 
   
   public TaskRejectedException(String message) {
     super(message);
   }
 
 
 
 
   
   public TaskRejectedException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\TaskRejectedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */