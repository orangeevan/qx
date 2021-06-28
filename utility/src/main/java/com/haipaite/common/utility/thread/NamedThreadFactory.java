 package com.haipaite.common.utility.thread;
 
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 
 
 public class NamedThreadFactory
   implements ThreadFactory
 {
   final ThreadGroup group;
   final AtomicInteger threadNumber = new AtomicInteger(1);
   final String namePrefix;
   
   public NamedThreadFactory(ThreadGroup group, String name) {
     this.group = group;
     this.namePrefix = group.getName() + ":" + name;
   }
   
   public Thread newThread(Runnable r) {
     Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
     return t;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\thread\NamedThreadFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */