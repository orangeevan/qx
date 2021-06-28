 package com.haipaite.common.scheduler.impl;

 import java.util.AbstractQueue;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 import java.util.PriorityQueue;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.Delayed;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.Condition;
 import java.util.concurrent.locks.ReentrantLock;







 public class FixTimeDelayQueue<E extends Delayed>
   extends AbstractQueue<E>
   implements BlockingQueue<E>
 {
   private final transient ReentrantLock lock = new ReentrantLock();
   private final transient Condition available = this.lock.newCondition();

   private final PriorityQueue<E> q = new PriorityQueue<>();



   private final long delayTime;




   public FixTimeDelayQueue(long delayTime) {
     this.delayTime = delayTime;
   }






   public FixTimeDelayQueue(long delayTime, Collection<? extends E> c) {
     this.delayTime = delayTime;
     addAll(c);
   }



   public E take() throws InterruptedException {
     ReentrantLock lock = this.lock;
     lock.lockInterruptibly();
     try {
       while (true) {
         Delayed delayed1 = (Delayed)this.q.peek();
         if (delayed1 == null) {
           this.available.await(); continue;
         }
         long delay = delayed1.getDelay(TimeUnit.MILLISECONDS);
         if (delay > 0L) {


           boolean flag = this.available.await((delay < this.delayTime) ? delay : this.delayTime, TimeUnit.MILLISECONDS);
           if (!flag);
           continue;
         }
         break;
       }
       Delayed delayed = (Delayed)this.q.poll();
       assert delayed != null;
       if (this.q.size() != 0)
         this.available.signalAll();
       return (E)delayed;

     }
     finally {

       lock.unlock();
     }
   }

   public E poll(long timeout, TimeUnit unit) throws InterruptedException {
     ReentrantLock lock = this.lock;
     lock.lockInterruptibly();
     long millis = unit.toMillis(timeout);
     try {
       while (true) {
         Delayed delayed1 = (Delayed)this.q.peek();
         if (delayed1 == null) {
           if (millis <= 0L) {
             return null;
           }
           long s = System.currentTimeMillis();
           boolean flag = this.available.await((this.delayTime > millis) ? millis : this.delayTime, TimeUnit.MILLISECONDS);
           millis -= System.currentTimeMillis() - s;
           if (!flag);


           continue;
         }

         long delay = delayed1.getDelay(TimeUnit.MILLISECONDS);
         if (delay > 0L) {
           if (millis <= 0L)
             return null;
           if (delay > millis) {
             delay = millis;
           }
           if (delay > this.delayTime) {
             delay = this.delayTime;
           }




           long s = System.currentTimeMillis();
           boolean flag = this.available.await(delay, TimeUnit.MILLISECONDS);
           millis -= System.currentTimeMillis() - s;
           if (!flag)
           {
             return null; }  continue;
         }  break;
       }
       Delayed delayed = (Delayed)this.q.poll();
       assert delayed != null;
       if (this.q.size() != 0)
         this.available.signalAll();
       return (E)delayed;

     }
     finally {

       lock.unlock();
     }
   }



   public boolean add(E e) {
     return offer(e);
   }

   public boolean offer(E e) {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       Delayed delayed = (Delayed)this.q.peek();
       this.q.offer(e);
       if (delayed == null || e.compareTo(delayed) < 0)
         this.available.signalAll();
       return true;
     } finally {
       lock.unlock();
     }
   }

   public void put(E e) {
     offer(e);
   }

   public boolean offer(E e, long timeout, TimeUnit unit) {
     return offer(e);
   }

   public E poll() {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       Delayed delayed1 = (Delayed)this.q.peek();
       if (delayed1 == null || delayed1.getDelay(TimeUnit.NANOSECONDS) > 0L) {
         return null;
       }
       Delayed delayed2 = (Delayed)this.q.poll();
       assert delayed2 != null;
       if (this.q.size() != 0)
         this.available.signalAll();
       return (E)delayed2;
     } finally {

       lock.unlock();
     }
   }

   public E peek() {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       return this.q.peek();
     } finally {
       lock.unlock();
     }
   }

   public int size() {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       return this.q.size();
     } finally {
       lock.unlock();
     }
   }

   public int drainTo(Collection<? super E> c) {
     if (c == null)
       throw new NullPointerException();
     if (c == this)
       throw new IllegalArgumentException();
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       int n = 0;
       while (true) {
         Delayed delayed = (Delayed)this.q.peek();
         if (delayed == null || delayed.getDelay(TimeUnit.NANOSECONDS) > 0L)
           break;
         c.add(this.q.poll());
         n++;
       }
       if (n > 0)
         this.available.signalAll();
       return n;
     } finally {
       lock.unlock();
     }
   }

   public int drainTo(Collection<? super E> c, int maxElements) {
     if (c == null)
       throw new NullPointerException();
     if (c == this)
       throw new IllegalArgumentException();
     if (maxElements <= 0)
       return 0;
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       int n = 0;
       while (n < maxElements) {
         Delayed delayed = (Delayed)this.q.peek();
         if (delayed == null || delayed.getDelay(TimeUnit.NANOSECONDS) > 0L)
           break;
         c.add(this.q.poll());
         n++;
       }
       if (n > 0)
         this.available.signalAll();
       return n;
     } finally {
       lock.unlock();
     }
   }

   public void clear() {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       this.q.clear();
     } finally {
       lock.unlock();
     }
   }

   public int remainingCapacity() {
     return Integer.MAX_VALUE;
   }

   public Object[] toArray() {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       return this.q.toArray();
     } finally {
       lock.unlock();
     }
   }

   public <T> T[] toArray(T[] a) {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       return (T[])this.q.toArray((Object[])a);
     } finally {
       lock.unlock();
     }
   }

   public boolean remove(Object o) {
     ReentrantLock lock = this.lock;
     lock.lock();
     try {
       return this.q.remove(o);
     } finally {
       lock.unlock();
     }
   }

   public Iterator<E> iterator() {
     return new Itr(toArray());
   }

   private class Itr implements Iterator<E> {
     final Object[] array;
     int cursor;
     int lastRet;

     Itr(Object[] array) {
       this.lastRet = -1;
       this.array = array;
     }

     public boolean hasNext() {
       return (this.cursor < this.array.length);
     }


     public E next() {
       if (this.cursor >= this.array.length)
         throw new NoSuchElementException();
       this.lastRet = this.cursor;
       return (E)this.array[this.cursor++];
     }

     public void remove() {
       if (this.lastRet < 0)
         throw new IllegalStateException();
       Object x = this.array[this.lastRet];
       this.lastRet = -1;
       FixTimeDelayQueue.this.lock.lock();
       try {
         for (Iterator it = FixTimeDelayQueue.this.q.iterator(); it.hasNext();) {
           if (it.next() == x) {
             it.remove();
             return;
           }
         }
       } finally {
         FixTimeDelayQueue.this.lock.unlock();
       }
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\FixTimeDelayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */