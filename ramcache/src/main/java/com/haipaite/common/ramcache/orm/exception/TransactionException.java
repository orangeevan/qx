 package com.haipaite.common.ramcache.orm.exception;
 
 
 
 
 
 
 public class TransactionException
   extends OrmException
 {
   private static final long serialVersionUID = -8396525701135532677L;
   
   public TransactionException() {}
   
   public TransactionException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public TransactionException(String message) {
     super(message);
   }
   
   public TransactionException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\exception\TransactionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */