 package com.haipaite.common.scheduler.impl;
 
 import java.util.AbstractCollection;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.Callable;
 import java.util.concurrent.Delayed;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.FutureTask;
 import java.util.concurrent.RunnableScheduledFuture;
 import java.util.concurrent.ScheduledExecutorService;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicLong;
 
 
 
 
 public class FixScheduledThreadPoolExecutor
   extends FixThreadPoolExecutor
   implements ScheduledExecutorService
 {
   private volatile boolean continueExistingPeriodicTasksAfterShutdown;
   private volatile boolean executeExistingDelayedTasksAfterShutdown;
   
   public FixScheduledThreadPoolExecutor(long delayTime) {
     super(1, 2147483647, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(delayTime));
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     this.executeExistingDelayedTasksAfterShutdown = true; } public FixScheduledThreadPoolExecutor(Integer poolSize, Long delayTime) { super(poolSize.intValue(), 2147483647, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(delayTime.longValue())); this.executeExistingDelayedTasksAfterShutdown = true; } public FixScheduledThreadPoolExecutor(Integer poolSize, Long delayTime, ThreadFactory threadFactory) { super(poolSize.intValue(), 2147483647, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(delayTime.longValue()), threadFactory); this.executeExistingDelayedTasksAfterShutdown = true; }
 
 
 
 
   
   private static final AtomicLong sequencer = new AtomicLong(0L);
 
 
 
   
   final long now() {
     return System.currentTimeMillis();
   }
 
 
 
   
   private class ScheduledFutureTask<V>
     extends FutureTask<V>
     implements RunnableScheduledFuture<V>
   {
     private final long sequenceNumber;
 
     
     private long time;
 
     
     private final long period;
 
 
     
     ScheduledFutureTask(Runnable r, V result, long ns) {
       super(r, result);
       this.time = ns;
       this.period = 0L;
       this.sequenceNumber = FixScheduledThreadPoolExecutor.sequencer.getAndIncrement();
     }
 
 
 
     
     ScheduledFutureTask(Runnable r, V result, long ns, long period) {
       super(r, result);
       this.time = ns;
       this.period = period;
       this.sequenceNumber = FixScheduledThreadPoolExecutor.sequencer.getAndIncrement();
     }
 
 
 
     
     ScheduledFutureTask(Callable<V> callable, long ns) {
       super(callable);
       this.time = ns;
       this.period = 0L;
       this.sequenceNumber = FixScheduledThreadPoolExecutor.sequencer.getAndIncrement();
     }
     
     public long getDelay(TimeUnit unit) {
       long d = unit.convert(this.time - FixScheduledThreadPoolExecutor.this.now(), TimeUnit.MILLISECONDS);
       return d;
     }
     
     public int compareTo(Delayed other) {
       if (other == this)
         return 0; 
       if (other instanceof ScheduledFutureTask) {
         ScheduledFutureTask<?> x = (ScheduledFutureTask)other;
         long diff = this.time - x.time;
         if (diff < 0L)
           return -1; 
         if (diff > 0L)
           return 1; 
         if (this.sequenceNumber < x.sequenceNumber) {
           return -1;
         }
         return 1;
       } 
       long d = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
       return (d == 0L) ? 0 : ((d < 0L) ? -1 : 1);
     }
 
     
     public boolean equals(Object obj) {
       return (this == obj);
     }
 
 
 
 
     
     public boolean isPeriodic() {
       return (this.period != 0L);
     }
 
 
 
     
     private void runPeriodic() {
       boolean ok = runAndReset();
       boolean down = FixScheduledThreadPoolExecutor.this.isShutdown();
       
       if (ok && (!down || (FixScheduledThreadPoolExecutor.this
         .getContinueExistingPeriodicTasksAfterShutdownPolicy() && !FixScheduledThreadPoolExecutor.this.isStopped()))) {
         long p = this.period;
         if (p > 0L) {
           this.time += p;
         } else {
           this.time = FixScheduledThreadPoolExecutor.this.now() - p;
         }  FixScheduledThreadPoolExecutor.this.getQueue().add(this);
 
       
       }
       else if (down) {
         FixScheduledThreadPoolExecutor.this.interruptIdleWorkers();
       } 
     }
 
 
     
     public void run() {
       if (isPeriodic()) {
         runPeriodic();
       } else {
         run();
       } 
     }
   }
 
 
   
   private void delayedExecute(Runnable command) {
     if (isShutdown()) {
       reject(command);
 
       
       return;
     } 
     
     if (getPoolSize() < getCorePoolSize()) {
       prestartCoreThread();
     }
     super.getQueue().add(command);
   }
 
 
 
 
   
   private void cancelUnwantedTasks() {
     boolean keepDelayed = getExecuteExistingDelayedTasksAfterShutdownPolicy();
     boolean keepPeriodic = getContinueExistingPeriodicTasksAfterShutdownPolicy();
     if (!keepDelayed && !keepPeriodic) {
       super.getQueue().clear();
     } else if (keepDelayed || keepPeriodic) {
       Object[] entries = super.getQueue().toArray();
       for (int i = 0; i < entries.length; i++) {
         Object e = entries[i];
         if (e instanceof RunnableScheduledFuture) {
           RunnableScheduledFuture<?> t = (RunnableScheduledFuture)e;
           if (t.isPeriodic() ? !keepPeriodic : !keepDelayed)
             t.cancel(false); 
         } 
       } 
       purge();
     } 
   }
   
   public boolean remove(Runnable task) {
     if (!(task instanceof RunnableScheduledFuture))
       return false; 
     return getQueue().remove(task);
   }
 
 
 
 
 
 
 
 
 
 
   
   protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
     return task;
   }
 
 
 
 
 
 
 
 
 
 
   
   protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
     return task;
   }
   
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
     if (command == null || unit == null)
       throw new NullPointerException(); 
     if (delay < 0L)
       delay = 0L; 
     long triggerTime = now() + unit.toMillis(delay);
     RunnableScheduledFuture<?> t = decorateTask(command, new ScheduledFutureTask(command, null, triggerTime));
     
     delayedExecute(t);
     return t;
   }
   
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
     if (callable == null || unit == null)
       throw new NullPointerException(); 
     if (delay < 0L)
       delay = 0L; 
     long triggerTime = now() + unit.toMillis(delay);
     RunnableScheduledFuture<V> t = decorateTask(callable, new ScheduledFutureTask<>(callable, triggerTime));
     
     delayedExecute(t);
     return t;
   }
 
   
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
     if (command == null || unit == null)
       throw new NullPointerException(); 
     if (period <= 0L)
       throw new IllegalArgumentException(); 
     if (initialDelay < 0L)
       initialDelay = 0L; 
     long triggerTime = now() + unit.toMillis(initialDelay);
     RunnableScheduledFuture<?> t = decorateTask(command, new ScheduledFutureTask(command, null, triggerTime, unit
           .toMillis(period)));
     delayedExecute(t);
     return t;
   }
 
   
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
     if (command == null || unit == null)
       throw new NullPointerException(); 
     if (delay <= 0L)
       throw new IllegalArgumentException(); 
     if (initialDelay < 0L)
       initialDelay = 0L; 
     long triggerTime = now() + unit.toMillis(initialDelay);
     RunnableScheduledFuture<?> t = decorateTask(command, new ScheduledFutureTask(command, null, triggerTime, unit
           .toMillis(-delay)));
     delayedExecute(t);
     return t;
   }
 
 
 
 
 
 
 
 
 
 
 
   
   public void execute(Runnable command) {
     if (command == null)
       throw new NullPointerException(); 
     schedule(command, 0L, TimeUnit.MILLISECONDS);
   }
 
 
   
   public Future<?> submit(Runnable task) {
     return schedule(task, 0L, TimeUnit.MILLISECONDS);
   }
   
   public <T> Future<T> submit(Runnable task, T result) {
     return schedule(Executors.callable(task, result), 0L, TimeUnit.MILLISECONDS);
   }
   
   public <T> Future<T> submit(Callable<T> task) {
     return schedule(task, 0L, TimeUnit.MILLISECONDS);
   }
 
 
 
 
 
 
 
 
 
   
   public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value) {
     this.continueExistingPeriodicTasksAfterShutdown = value;
     if (!value && isShutdown()) {
       cancelUnwantedTasks();
     }
   }
 
 
 
 
 
 
 
 
   
   public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
     return this.continueExistingPeriodicTasksAfterShutdown;
   }
 
 
 
 
 
 
 
 
   
   public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) {
     this.executeExistingDelayedTasksAfterShutdown = value;
     if (!value && isShutdown()) {
       cancelUnwantedTasks();
     }
   }
 
 
 
 
 
 
 
   
   public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
     return this.executeExistingDelayedTasksAfterShutdown;
   }
 
 
 
 
 
 
 
 
 
 
   
   public void shutdown() {
     cancelUnwantedTasks();
     super.shutdown();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public List<Runnable> shutdownNow() {
     return super.shutdownNow();
   }
 
 
 
 
 
 
 
 
 
   
   public BlockingQueue<Runnable> getQueue() {
     return super.getQueue();
   }
 
 
   
   private static class DelayedWorkQueue
     extends AbstractCollection<Runnable>
     implements BlockingQueue<Runnable>
   {
     private final FixTimeDelayQueue<RunnableScheduledFuture> dq;
 
 
     
     public DelayedWorkQueue(long delayTime) {
       this.dq = new FixTimeDelayQueue<>(delayTime);
     }
     
     public Runnable poll() {
       return this.dq.poll();
     }
     
     public Runnable peek() {
       return this.dq.peek();
     }
     
     public Runnable take() throws InterruptedException {
       return this.dq.take();
     }
     
     public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
       return this.dq.poll(timeout, unit);
     }
     
     public boolean add(Runnable x) {
       return this.dq.add((RunnableScheduledFuture)x);
     }
     
     public boolean offer(Runnable x) {
       return this.dq.offer((RunnableScheduledFuture)x);
     }
     
     public void put(Runnable x) {
       this.dq.put((RunnableScheduledFuture)x);
     }
     
     public boolean offer(Runnable x, long timeout, TimeUnit unit) {
       return this.dq.offer((RunnableScheduledFuture)x, timeout, unit);
     }
     
     public Runnable remove() {
       return this.dq.remove();
     }
     
     public Runnable element() {
       return this.dq.element();
     }
     
     public void clear() {
       this.dq.clear();
     }
     
     public int drainTo(Collection<? super Runnable> c) {
       return this.dq.drainTo(c);
     }
     
     public int drainTo(Collection<? super Runnable> c, int maxElements) {
       return this.dq.drainTo(c, maxElements);
     }
     
     public int remainingCapacity() {
       return this.dq.remainingCapacity();
     }
     
     public boolean remove(Object x) {
       return this.dq.remove(x);
     }
     
     public boolean contains(Object x) {
       return this.dq.contains(x);
     }
     
     public int size() {
       return this.dq.size();
     }
     
     public boolean isEmpty() {
       return this.dq.isEmpty();
     }
     
     public Object[] toArray() {
       return this.dq.toArray();
     }
     
     public <T> T[] toArray(T[] array) {
       return this.dq.toArray(array);
     }
     
     public Iterator<Runnable> iterator() {
       return new Iterator<Runnable>() {
           private Iterator<RunnableScheduledFuture> it = FixScheduledThreadPoolExecutor.DelayedWorkQueue.this.dq.iterator();
           
           public boolean hasNext() {
             return this.it.hasNext();
           }
           
           public Runnable next() {
             return this.it.next();
           }
           
           public void remove() {
             this.it.remove();
           }
         };
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\FixScheduledThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */