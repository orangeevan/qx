 package com.haipaite.common.utility;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Result<T>
   implements Serializable
 {
   private static final long serialVersionUID = 8855012718532767136L;
   public static final int SUCCESS = 0;
   public static final String MAP_KEY_CODE = "code";
   public static final String MAP_KEY_CONTENT = "content";
   private int code;
   private T content;
   
   public static <T> Result<T> SUCCESS(int code) {
     return new Result<>(code);
   }
 
 
 
 
 
 
 
   
   public static <T> Result<T> SUCCESS(T content) {
     return new Result<>(0, content);
   }
 
 
 
 
 
 
 
   
   public static <T> Result<T> ERROR(int code) {
     return new Result<>(code);
   }
 
 
 
 
 
 
 
 
 
   
   public static <T> Result<T> ERROR(int code, T content) {
     return new Result<>(code, content);
   }
   
   public Map<String, Object> toMap() {
     Map<String, Object> result = new HashMap<>(2);
     result.put("code", Integer.valueOf(this.code));
     result.put("content", this.content);
     return result;
   }
 
   
   public Result() {}
   
   public Result(int code) {
     this.code = code;
     this.content = null;
   }
   
   public Result(int code, T content) {
     this.code = code;
     this.content = content;
   }
 
 
 
 
   
   public int getCode() {
     return this.code;
   }
 
 
 
 
   
   public T getContent() {
     return this.content;
   }
   
   public void setCode(int code) {
     this.code = code;
   }
   
   public void setContent(T content) {
     this.content = content;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */