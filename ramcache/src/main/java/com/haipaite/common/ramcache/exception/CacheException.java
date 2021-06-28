 package com.haipaite.common.ramcache.exception;
 
 
 
 
 
 
 public abstract class CacheException
   extends RuntimeException
 {
   private static final long serialVersionUID = -1115472626914115272L;
   
   public CacheException() {}
   
   public CacheException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public CacheException(String message) {
     super(message);
   }
   
   public CacheException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\exception\CacheException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */