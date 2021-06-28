 package com.haipaite.common.ramcache.orm.exception;
 
 
 
 
 
 
 public class DataException
   extends OrmException
 {
   private static final long serialVersionUID = -8396525701135532677L;
   
   public DataException() {}
   
   public DataException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public DataException(String message) {
     super(message);
   }
   
   public DataException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\exception\DataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */