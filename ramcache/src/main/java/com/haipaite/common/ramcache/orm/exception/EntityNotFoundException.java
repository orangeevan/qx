 package com.haipaite.common.ramcache.orm.exception;
 
 
 
 
 
 
 public class EntityNotFoundException
   extends OrmException
 {
   private static final long serialVersionUID = -2034117261159389354L;
   
   public EntityNotFoundException() {}
   
   public EntityNotFoundException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public EntityNotFoundException(String message) {
     super(message);
   }
   
   public EntityNotFoundException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\exception\EntityNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */