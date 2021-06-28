 package com.haipaite.common.ramcache.exception;






 public class UniqueFieldException
   extends CacheException
 {
   private static final long serialVersionUID = 9191882390957957633L;

   public UniqueFieldException() {}

   public UniqueFieldException(String message, Throwable cause) {
     super(message, cause);
   }

   public UniqueFieldException(String message) {
     super(message);
   }

   public UniqueFieldException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\exception\UniqueFieldException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */