 package com.haipaite.common.ramcache.lock;
 
 import java.util.WeakHashMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReentrantLock;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ObjectLockHolder
 {
   public class Holder
   {
     private final Class clz;
     private final Lock tieLock = new ReentrantLock();
     
     private final WeakHashMap<Object, ObjectLock> locks = new WeakHashMap<>();
 
 
 
 
     
     public Holder(Class clz) {
       this.clz = clz;
     }
 
 
 
 
 
     
     public ObjectLock getLock(Object object) {
       ObjectLock result = this.locks.get(object);
       if (result != null) {
         return result;
       }
       return createLock(object);
     }
 
 
 
 
 
     
     private synchronized ObjectLock createLock(Object object) {
       ObjectLock result = this.locks.get(object);
       if (result != null) {
         return result;
       }
       result = new ObjectLock(object);
       this.locks.put(object, result);
       return result;
     }
 
 
 
 
     
     public Lock getTieLock() {
       return this.tieLock;
     }
 
 
 
 
     
     public int count() {
       return this.locks.size();
     }
   }
 
   
   private ConcurrentHashMap<Class, Holder> holders = new ConcurrentHashMap<>();
 
 
 
 
 
   
   public ObjectLock getLock(Object object) {
     Holder holder = getHolder(object.getClass());
     ObjectLock lock = holder.getLock(object);
     return lock;
   }
 
 
 
 
 
   
   private Holder getHolder(Class clz) {
     Holder holder = this.holders.get(clz);
     if (holder != null) {
       return holder;
     }
     this.holders.putIfAbsent(clz, new Holder(clz));
     return this.holders.get(clz);
   }
 
 
 
 
 
   
   public Lock getTieLock(Class clz) {
     Holder holder = getHolder(clz);
     return holder.getTieLock();
   }
 
 
 
 
 
   
   public int count(Class<?> clz) {
     if (this.holders.containsKey(clz)) {
       Holder holder = getHolder(clz);
       return holder.count();
     } 
     return 0;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\lock\ObjectLockHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */