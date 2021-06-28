 package com.haipaite.common.ramcache.orm.exception;






 public abstract class OrmException
   extends RuntimeException
 {
   private static final long serialVersionUID = -5480573723994246089L;

   public OrmException() {}

   public OrmException(String message, Throwable cause) {
     super(message, cause);
   }

   public OrmException(String message) {
     super(message);
   }

   public OrmException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\exception\OrmException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */