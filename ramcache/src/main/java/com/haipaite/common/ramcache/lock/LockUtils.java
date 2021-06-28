 package com.haipaite.common.ramcache.lock;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.TreeSet;
 import java.util.concurrent.locks.Lock;





 public class LockUtils
 {
   private static ObjectLockHolder holder = new ObjectLockHolder();







   public static ChainLock getLock(Object... objects) {
     List<? extends Lock> locks = loadLocks(objects);
     return new ChainLock(locks);
   }







   public static List<? extends Lock> loadLocks(Object... objects) {
     List<ObjectLock> locks = new ArrayList<>();
     for (Object obj : objects) {
       ObjectLock lock = holder.getLock(obj);
       locks.add(lock);
     }
     Collections.sort(locks);


     TreeSet<Integer> idx = new TreeSet<>();
     Integer start = null;
     for (int i = 0; i < locks.size(); i++) {
       if (start == null) {
         start = Integer.valueOf(i);
       } else {

         ObjectLock lock1 = locks.get(start.intValue());
         ObjectLock lock2 = locks.get(i);
         if (lock1.isTie(lock2)) {
           idx.add(start);
         } else {

           start = Integer.valueOf(i);
         }
       }
     }  if (idx.size() == 0) {
       return (List)locks;
     }


     List<Lock> news = new ArrayList<>(locks.size() + idx.size());
     news.addAll((Collection)locks);
     Iterator<Integer> it = idx.descendingIterator();
     while (it.hasNext()) {
       Integer integer = it.next();
       ObjectLock lock = locks.get(integer.intValue());
       Lock tieLock = holder.getTieLock(lock.getClz());
       news.add(integer.intValue(), tieLock);
     }
     return news;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\lock\LockUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */