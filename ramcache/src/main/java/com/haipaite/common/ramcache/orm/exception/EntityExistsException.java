 package com.haipaite.common.ramcache.orm.exception;







 public class EntityExistsException
   extends OrmException
 {
   private static final long serialVersionUID = -4856143234643053387L;

   public EntityExistsException() {}

   public EntityExistsException(String message, Throwable cause) {
     super(message, cause);
   }

   public EntityExistsException(String message) {
     super(message);
   }

   public EntityExistsException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\exception\EntityExistsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */