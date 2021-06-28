 package com.haipaite.common.scheduler.impl;
 
 import java.util.ArrayList;
 import java.util.ConcurrentModificationException;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.concurrent.AbstractExecutorService;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.RejectedExecutionException;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.Condition;
 import java.util.concurrent.locks.ReentrantLock;
 
 
 
 
 
 
 
 
 
 
 public class FixThreadPoolExecutor
   extends AbstractExecutorService
 {
   private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
 
 
   
   volatile int runState;
 
   
   static final int RUNNING = 0;
 
   
   static final int SHUTDOWN = 1;
 
   
   static final int STOP = 2;
 
   
   static final int TERMINATED = 3;
 
   
   private final BlockingQueue<Runnable> workQueue;
 
   
   private final ReentrantLock mainLock = new ReentrantLock();
 
 
 
   
   private final Condition termination = this.mainLock.newCondition();
 
 
 
 
   
   private final HashSet<Worker> workers = new HashSet<>();
 
 
 
 
 
   
   private volatile long keepAliveTime;
 
 
 
 
 
   
   private volatile boolean allowCoreThreadTimeOut;
 
 
 
 
 
   
   private volatile int corePoolSize;
 
 
 
 
 
   
   private volatile int maximumPoolSize;
 
 
 
 
 
   
   private volatile int poolSize;
 
 
 
 
   
   private volatile RejectedExecutionHandler handler;
 
 
 
 
   
   private volatile ThreadFactory threadFactory;
 
 
 
 
   
   private int largestPoolSize;
 
 
 
 
   
   private long completedTaskCount;
 
 
 
 
   
   private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public FixThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, 
         Executors.defaultThreadFactory(), defaultHandler);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public FixThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public FixThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, 
         Executors.defaultThreadFactory(), handler);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public FixThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
     if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0L)
     {
       throw new IllegalArgumentException(); } 
     if (workQueue == null || threadFactory == null || handler == null)
       throw new NullPointerException(); 
     this.corePoolSize = corePoolSize;
     this.maximumPoolSize = maximumPoolSize;
     this.workQueue = workQueue;
     this.keepAliveTime = unit.toNanos(keepAliveTime);
     this.threadFactory = threadFactory;
     this.handler = handler;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void execute(Runnable command) {
     if (command == null)
       throw new NullPointerException(); 
     if (this.poolSize >= this.corePoolSize || !addIfUnderCorePoolSize(command)) {
       if (this.runState == 0 && this.workQueue.offer(command)) {
         if (this.runState != 0 || this.poolSize == 0)
           ensureQueuedTaskHandled(command); 
       } else if (!addIfUnderMaximumPoolSize(command)) {
         reject(command);
       } 
     }
   }
 
 
 
 
 
 
   
   private Thread addThread(Runnable firstTask) {
     Worker w = new Worker(firstTask);
     Thread t = this.threadFactory.newThread(w);
     if (t != null) {
       w.thread = t;
       this.workers.add(w);
       int nt = ++this.poolSize;
       if (nt > this.largestPoolSize)
         this.largestPoolSize = nt; 
     } 
     return t;
   }
 
 
 
 
 
 
 
 
   
   private boolean addIfUnderCorePoolSize(Runnable firstTask) {
     Thread t = null;
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       if (this.poolSize < this.corePoolSize && this.runState == 0)
         t = addThread(firstTask); 
     } finally {
       mainLock.unlock();
     } 
     if (t == null)
       return false; 
     t.start();
     return true;
   }
 
 
 
 
 
 
 
 
   
   private boolean addIfUnderMaximumPoolSize(Runnable firstTask) {
     Thread t = null;
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       if (this.poolSize < this.maximumPoolSize && this.runState == 0)
         t = addThread(firstTask); 
     } finally {
       mainLock.unlock();
     } 
     if (t == null)
       return false; 
     t.start();
     return true;
   }
 
 
 
 
 
 
 
 
 
   
   private void ensureQueuedTaskHandled(Runnable command) {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     boolean reject = false;
     Thread t = null;
     
     try { int state = this.runState;
       if (state != 0 && this.workQueue.remove(command)) {
         reject = true;
       } else if (state < 2 && this.poolSize < Math.max(this.corePoolSize, 1) && !this.workQueue.isEmpty()) {
         t = addThread(null);
       }  }
     finally { mainLock.unlock(); }
     
     if (reject) {
       reject(command);
     } else if (t != null) {
       t.start();
     } 
   }
 
 
   
   void reject(Runnable command) {
     this.handler.rejectedExecution(command, this);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   private final class Worker
     implements Runnable
   {
     private final ReentrantLock runLock = new ReentrantLock();
 
 
 
     
     private Runnable firstTask;
 
 
 
     
     volatile long completedTasks;
 
 
     
     Thread thread;
 
 
 
     
     Worker(Runnable firstTask) {
       this.firstTask = firstTask;
     }
     
     boolean isActive() {
       return this.runLock.isLocked();
     }
 
 
 
     
     void interruptIfIdle() {
       ReentrantLock runLock = this.runLock;
       if (runLock.tryLock()) {
         try {
           if (this.thread != Thread.currentThread())
             this.thread.interrupt(); 
         } finally {
           runLock.unlock();
         } 
       }
     }
 
 
 
     
     void interruptNow() {
       this.thread.interrupt();
     }
 
 
 
     
     private void runTask(Runnable task) {
       ReentrantLock runLock = this.runLock;
       runLock.lock();
 
 
 
       
       try {
         if (FixThreadPoolExecutor.this.runState < 2 && Thread.interrupted() && FixThreadPoolExecutor.this.runState >= 2) {
           this.thread.interrupt();
         }
 
 
 
         
         boolean ran = false;
         FixThreadPoolExecutor.this.beforeExecute(this.thread, task);
         try {
           task.run();
           ran = true;
           FixThreadPoolExecutor.this.afterExecute(task, null);
           this.completedTasks++;
         } catch (RuntimeException ex) {
           if (!ran)
             FixThreadPoolExecutor.this.afterExecute(task, ex); 
           throw ex;
         } 
       } finally {
         runLock.unlock();
       } 
     }
 
 
 
     
     public void run() {
       try {
         Runnable task = this.firstTask;
         this.firstTask = null;
         while (task != null || (task = FixThreadPoolExecutor.this.getTask()) != null) {
           runTask(task);
           task = null;
         } 
       } finally {
         FixThreadPoolExecutor.this.workerDone(this);
       } 
     }
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   Runnable getTask() {
     while (true) {
       try {
         Runnable r;
         int state = this.runState;
         if (state > 1) {
           return null;
         }
         if (state == 1) {
           r = this.workQueue.poll();
         } else if (this.poolSize > this.corePoolSize || this.allowCoreThreadTimeOut) {
           r = this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS);
         } else {
           r = this.workQueue.take();
         }  if (r != null)
           return r; 
         if (workerCanExit()) {
           if (this.runState >= 1)
             interruptIdleWorkers(); 
           return null;
         }
       
       } catch (InterruptedException interruptedException) {}
     } 
   }
 
 
 
 
 
 
 
   
   private boolean workerCanExit() {
     boolean canExit;
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
 
     
     try {
       canExit = (this.runState >= 2 || this.workQueue.isEmpty() || (this.allowCoreThreadTimeOut && this.poolSize > Math.max(1, this.corePoolSize)));
     } finally {
       mainLock.unlock();
     } 
     return canExit;
   }
 
 
 
 
 
   
   void interruptIdleWorkers() {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       for (Worker w : this.workers)
         w.interruptIfIdle(); 
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
   
   void workerDone(Worker w) {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       this.completedTaskCount += w.completedTasks;
       this.workers.remove(w);
       if (--this.poolSize == 0)
         tryTerminate(); 
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
   
   private void tryTerminate() {
     if (this.poolSize == 0) {
       int state = this.runState;
       if (state < 2 && !this.workQueue.isEmpty()) {
         state = 0;
         Thread t = addThread(null);
         if (t != null)
           t.start(); 
       } 
       if (state == 2 || state == 1) {
         this.runState = 3;
         this.termination.signalAll();
         terminated();
       } 
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void shutdown() {
     SecurityManager security = System.getSecurityManager();
     if (security != null) {
       security.checkPermission(shutdownPerm);
     }
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       if (security != null) {
         for (Worker w : this.workers) {
           security.checkAccess(w.thread);
         }
       }
       int state = this.runState;
       if (state < 1) {
         this.runState = 1;
       }
       try {
         for (Worker w : this.workers) {
           w.interruptIfIdle();
         }
       } catch (SecurityException se) {
         this.runState = state;
         
         throw se;
       } 
       
       tryTerminate();
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public List<Runnable> shutdownNow() {
     SecurityManager security = System.getSecurityManager();
     if (security != null) {
       security.checkPermission(shutdownPerm);
     }
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       if (security != null) {
         for (Worker w : this.workers) {
           security.checkAccess(w.thread);
         }
       }
       int state = this.runState;
       if (state < 2) {
         this.runState = 2;
       }
       try {
         for (Worker w : this.workers) {
           w.interruptNow();
         }
       } catch (SecurityException se) {
         this.runState = state;
         
         throw se;
       } 
       
       List<Runnable> tasks = drainQueue();
       tryTerminate();
       return tasks;
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
   
   private List<Runnable> drainQueue() {
     List<Runnable> taskList = new ArrayList<>();
     this.workQueue.drainTo(taskList);
 
 
 
 
     
     while (!this.workQueue.isEmpty()) {
       Iterator<Runnable> it = this.workQueue.iterator();
       try {
         if (it.hasNext()) {
           Runnable r = it.next();
           if (this.workQueue.remove(r))
             taskList.add(r); 
         } 
       } catch (ConcurrentModificationException concurrentModificationException) {}
     } 
     
     return taskList;
   }
   
   public boolean isShutdown() {
     return (this.runState != 0);
   }
 
 
 
 
   
   boolean isStopped() {
     return (this.runState == 2);
   }
 
 
 
 
 
 
 
 
 
   
   public boolean isTerminating() {
     int state = this.runState;
     return (state == 1 || state == 2);
   }
   
   public boolean isTerminated() {
     return (this.runState == 3);
   }
   
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
     long nanos = unit.toNanos(timeout);
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       while (true) {
         if (this.runState == 3)
           return true; 
         if (nanos <= 0L)
           return false; 
         nanos = this.termination.awaitNanos(nanos);
       } 
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
   
   protected void finalize() {
     shutdown();
   }
 
 
 
 
 
 
 
 
   
   public void setThreadFactory(ThreadFactory threadFactory) {
     if (threadFactory == null)
       throw new NullPointerException(); 
     this.threadFactory = threadFactory;
   }
 
 
 
 
 
   
   public ThreadFactory getThreadFactory() {
     return this.threadFactory;
   }
 
 
 
 
 
 
   
   public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
     if (handler == null)
       throw new NullPointerException(); 
     this.handler = handler;
   }
 
 
 
 
 
   
   public RejectedExecutionHandler getRejectedExecutionHandler() {
     return this.handler;
   }
 
 
 
 
 
 
 
 
 
 
   
   public void setCorePoolSize(int corePoolSize) {
     if (corePoolSize < 0)
       throw new IllegalArgumentException(); 
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       int extra = this.corePoolSize - corePoolSize;
       this.corePoolSize = corePoolSize;
       if (extra < 0) {
         int n = this.workQueue.size();
         while (extra++ < 0 && n-- > 0 && this.poolSize < corePoolSize) {
           Thread t = addThread(null);
           if (t != null) {
             t.start();
           }
         }
       
       } else if (extra > 0 && this.poolSize > corePoolSize) {
         try {
           Iterator<Worker> it = this.workers.iterator();
           while (it.hasNext() && extra-- > 0 && this.poolSize > corePoolSize && this.workQueue
             .remainingCapacity() == 0)
             ((Worker)it.next()).interruptIfIdle(); 
         } catch (SecurityException securityException) {}
       }
     
     } finally {
       
       mainLock.unlock();
     } 
   }
 
 
 
 
 
   
   public int getCorePoolSize() {
     return this.corePoolSize;
   }
 
 
 
 
 
 
 
   
   public boolean prestartCoreThread() {
     return addIfUnderCorePoolSize(null);
   }
 
 
 
 
 
 
   
   public int prestartAllCoreThreads() {
     int n = 0;
     while (addIfUnderCorePoolSize(null))
       n++; 
     return n;
   }
 
 
 
 
 
 
 
 
 
 
   
   public boolean allowsCoreThreadTimeOut() {
     return this.allowCoreThreadTimeOut;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void allowCoreThreadTimeOut(boolean value) {
     if (value && this.keepAliveTime <= 0L) {
       throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
     }
     this.allowCoreThreadTimeOut = value;
   }
 
 
 
 
 
 
 
 
 
   
   public void setMaximumPoolSize(int maximumPoolSize) {
     if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize)
       throw new IllegalArgumentException(); 
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       int extra = this.maximumPoolSize - maximumPoolSize;
       this.maximumPoolSize = maximumPoolSize;
       if (extra > 0 && this.poolSize > maximumPoolSize) {
         try {
           Iterator<Worker> it = this.workers.iterator();
           while (it.hasNext() && extra > 0 && this.poolSize > maximumPoolSize) {
             ((Worker)it.next()).interruptIfIdle();
             extra--;
           } 
         } catch (SecurityException securityException) {}
       }
     }
     finally {
       
       mainLock.unlock();
     } 
   }
 
 
 
 
 
   
   public int getMaximumPoolSize() {
     return this.maximumPoolSize;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void setKeepAliveTime(long time, TimeUnit unit) {
     if (time < 0L)
       throw new IllegalArgumentException(); 
     if (time == 0L && allowsCoreThreadTimeOut())
       throw new IllegalArgumentException("Core threads must have nonzero keep alive times"); 
     this.keepAliveTime = unit.toNanos(time);
   }
 
 
 
 
 
 
 
 
   
   public long getKeepAliveTime(TimeUnit unit) {
     return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
   }
 
 
 
 
 
 
 
 
 
   
   public BlockingQueue<Runnable> getQueue() {
     return this.workQueue;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public boolean remove(Runnable task) {
     return getQueue().remove(task);
   }
 
 
 
 
 
 
 
 
 
 
   
   public void purge() {
     try {
       Iterator<Runnable> it = getQueue().iterator();
       while (it.hasNext()) {
         Runnable r = it.next();
         if (r instanceof Future) {
           Future<?> c = (Future)r;
           if (c.isCancelled())
             it.remove(); 
         } 
       } 
     } catch (ConcurrentModificationException ex) {
       return;
     } 
   }
 
 
 
 
 
 
   
   public int getPoolSize() {
     return this.poolSize;
   }
 
 
 
 
 
   
   public int getActiveCount() {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       int n = 0;
       for (Worker w : this.workers) {
         if (w.isActive())
           n++; 
       } 
       return n;
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
   
   public int getLargestPoolSize() {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       return this.largestPoolSize;
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
 
 
   
   public long getTaskCount() {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       long n = this.completedTaskCount;
       for (Worker w : this.workers) {
         n += w.completedTasks;
         if (w.isActive())
           n++; 
       } 
       return n + this.workQueue.size();
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
 
 
   
   public long getCompletedTaskCount() {
     ReentrantLock mainLock = this.mainLock;
     mainLock.lock();
     try {
       long n = this.completedTaskCount;
       for (Worker w : this.workers)
         n += w.completedTasks; 
       return n;
     } finally {
       mainLock.unlock();
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   protected void beforeExecute(Thread t, Runnable r) {}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   protected void afterExecute(Runnable r, Throwable t) {}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   protected void terminated() {}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static class CallerRunsPolicy
     implements RejectedExecutionHandler
   {
     public void rejectedExecution(Runnable r, FixThreadPoolExecutor e) {
       if (!e.isShutdown()) {
         r.run();
       }
     }
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static class AbortPolicy
     implements RejectedExecutionHandler
   {
     public void rejectedExecution(Runnable r, FixThreadPoolExecutor e) {
       throw new RejectedExecutionException();
     }
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static class DiscardPolicy
     implements RejectedExecutionHandler
   {
     public void rejectedExecution(Runnable r, FixThreadPoolExecutor e) {}
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static class DiscardOldestPolicy
     implements RejectedExecutionHandler
   {
     public void rejectedExecution(Runnable r, FixThreadPoolExecutor e) {
       if (!e.isShutdown()) {
         e.getQueue().poll();
         e.execute(r);
       } 
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\FixThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */