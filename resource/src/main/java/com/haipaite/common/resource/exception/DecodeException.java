 package com.haipaite.common.resource.exception;
 
 
 public class DecodeException
   extends RuntimeException
 {
   private static final long serialVersionUID = 7375734754832836149L;
   
   public DecodeException() {}
   
   public DecodeException(String message, Throwable cause) {
     super(message, cause);
   }
   
   public DecodeException(String message) {
     super(message);
   }
   
   public DecodeException(Throwable cause) {
     super(cause);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\exception\DecodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */