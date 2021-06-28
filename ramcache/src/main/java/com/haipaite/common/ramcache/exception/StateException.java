 package com.haipaite.common.ramcache.exception;
 
 
 
 
 
 
 public class StateException
   extends CacheException
 {
   private static final long serialVersionUID = 3236441373096075879L;
   
   public StateException() {}
   
   public StateException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public StateException(String message) {
     super(message);
   }
   
   public StateException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\exception\StateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */