 package com.haipaite.common.ramcache.lock;

 import java.util.List;
 import java.util.concurrent.locks.Lock;












 public class ChainLock
 {
   private final Lock current;
   private final ChainLock next;

   public ChainLock(List<? extends Lock> locks) {
     if (locks == null || locks.size() == 0) {
       throw new IllegalArgumentException("构建锁链的锁数量不能为0");
     }
     this.current = locks.remove(0);
     if (locks.size() > 0) {
       this.next = new ChainLock(locks);
     } else {
       this.next = null;
     }
   }




   public void lock() {
     this.current.lock();
     if (this.next != null) {
       this.next.lock();
     }
   }




   public void unlock() {
     if (this.next != null) {
       this.next.unlock();
     }
     this.current.unlock();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\lock\ChainLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */