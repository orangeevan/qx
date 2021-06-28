 package com.haipaite.common.utility.test;
 
 import java.util.Queue;
 import java.util.concurrent.ConcurrentLinkedQueue;
 
 
 
 
 
 
 public abstract class AbstractMock
 {
   protected Queue<Object> queue = new ConcurrentLinkedQueue();
 
 
 
 
   
   public Object getRetValue() {
     Object e = this.queue.poll();
     if (e instanceof RuntimeException) {
       throw (RuntimeException)e;
     }
     return e;
   }
 
 
   
   public void addException(RuntimeException exception) {
     this.queue.add(exception);
   }
   
   public void addRetValue(Object retValue) {
     this.queue.add(retValue);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\test\AbstractMock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */