 package com.haipaite.common.ramcache.persist;
 
 import com.haipaite.common.ramcache.IEntity;
 import com.haipaite.common.ramcache.orm.Accessor;
 import com.haipaite.common.utility.DateUtils;
 import com.haipaite.common.utility.thread.NamedThreadFactory;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Date;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ScheduledThreadPoolExecutor;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicReference;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 public class TimingConsumer
   implements Runnable
 {
   private static final Logger logger = LoggerFactory.getLogger(TimingConsumer.class);
 
   
   private String name;
   
   private int period;
   
   private Accessor accessor;
   
   private TimingPersister owner;
   
   private Object lock = new Object();
 
   
   private AtomicReference<TimingConsumerState> state = new AtomicReference<>(TimingConsumerState.WAITING);
 
   
   private volatile boolean stoped;
   
   private Date nextTime;
   
   private AtomicInteger error = new AtomicInteger();
 
   
   private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
 
 
   
   static {
     ThreadGroup group = new ThreadGroup("定时任务");
     NamedThreadFactory threadFactory = new NamedThreadFactory(group, "定时存储任务");
     scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(50, (ThreadFactory)threadFactory);
   }
   
   public static void shutdownExecutor() {
     if (scheduledThreadPoolExecutor != null) {
       scheduledThreadPoolExecutor.shutdown();
     }
   }
   
   public TimingConsumer(String name, String period, Accessor accessor, TimingPersister owner) {
     this.name = name;
     this.period = Integer.valueOf(period).intValue();
     this.accessor = accessor;
     this.owner = owner;
     scheduledThreadPoolExecutor.scheduleAtFixedRate(this, 10L, this.period, TimeUnit.SECONDS);
   }
 
   
   public void run() {
     if (this.stoped) {
       return;
     }
     try {
       Collection<Element> elements = null;
       synchronized (this.lock) {
         elements = this.owner.clearElements();
         this.state.compareAndSet(TimingConsumerState.WAITING, TimingConsumerState.RUNNING);
         Date start = new Date();
         if (logger.isDebugEnabled()) {
           logger.debug("定时入库[{}]开始[{}]执行", this.name, DateUtils.date2String(start, "yyyy-MM-dd HH:mm:ss"));
         }
         if (elements.isEmpty()) {
           this.state.compareAndSet(TimingConsumerState.RUNNING, TimingConsumerState.WAITING);
           return;
         } 
         persist(elements);
         if (logger.isDebugEnabled()) {
           logger.debug("定时入库[{}]入库[{}]条数据耗时[{}]", new Object[] { this.name, 
                 Integer.valueOf(elements.size()), Long.valueOf(System.currentTimeMillis() - start.getTime()) });
         }
       } 
     } catch (Throwable e) {
       logger.error("Timing执行异常!", e);
     } finally {
       this.state.compareAndSet(TimingConsumerState.RUNNING, TimingConsumerState.WAITING);
     } 
   }
 
   
   private void persist(Collection<Element> elements) {
     List<IEntity> saveList = new ArrayList<>(500);
     List<IEntity> updateList = new ArrayList<>(500);
     List<IEntity> deleteList = new LinkedList<>();
     
     for (Element element : elements) {
       try {
         Class<? extends IEntity> clz = element.getEntityClass();
         switch (element.getType()) {
           
           case INSERT:
             if (element.getEntity().serialize())
             {
               saveList.add(element.getEntity());
             }
             break;
           case DELETE:
             this.accessor.remove(clz, element.getId());
             break;
 
           
           case UPDATE:
             if (element.getEntity().serialize())
             {
               updateList.add(element.getEntity());
             }
             break;
         } 
         
         Listener listener = this.owner.getListener(clz);
         if (listener != null) {
           listener.notify(element.getType(), true, element.getId(), element.getEntity(), null);
         }
       } catch (RuntimeException e) {
         this.error.getAndIncrement();
         if (logger.isWarnEnabled()) {
           logger.warn("实体更新队列[{}]处理元素[{}]时出现异常:{}", new Object[] { this.name, element, e.getMessage() });
         }
         Listener listener = this.owner.getListener(element.getEntityClass());
         if (listener != null) {
           listener.notify(element.getType(), false, element.getId(), element.getEntity(), e);
         }
       } catch (Exception e) {
         this.error.getAndIncrement();
         if (element == null) {
           logger.error("获取更新队列元素时线程被非法打断", e); continue;
         } 
         logger.error("更新队列处理出现未知异常", e);
       } 
     } 
 
     
     try {
       this.accessor.batchSave(saveList);
     } catch (Exception e) {
       logger.error("批量存储处理出现未知异常", e);
       
       for (IEntity temp : saveList) {
         try {
           this.accessor.save(null, temp);
         } catch (Exception e1) {
           logger.error("存储处理出现未知异常", e1);
         } 
       } 
     } 
     
     try {
       this.accessor.batchUpdate(updateList);
     } catch (Exception e) {
       logger.error("批量更新处理出现未知异常", e);
       
       for (IEntity temp : updateList) {
         try {
           this.accessor.update(null, temp);
         } catch (Exception e1) {
           logger.error("更新处理出现未知异常", e1);
         } 
       } 
     } 
     
     try {
       this.accessor.batchDelete(deleteList);
     } catch (Exception e) {
       logger.error("批量更新处理出现未知异常", e);
       
       for (IEntity temp : deleteList) {
         try {
           this.accessor.remove(temp.getClass(), temp.getId());
         } catch (Exception e1) {
           logger.error("更新处理出现未知异常", e1);
         } 
       } 
     } 
   }
 
   
   public TimingConsumerState getState() {
     return this.state.get();
   }
   
   public void stop() {
     if (logger.isDebugEnabled()) {
       logger.debug("定时入库[{}]收到停止通知", this.name);
     }
     synchronized (this.lock) {
       this.stoped = true;
       Collection<Element> elements = this.owner.clearElements();
       persist(elements);
       while (true) {
         if (this.state.compareAndSet(TimingConsumerState.WAITING, TimingConsumerState.STOPPED)) {
           return;
         }
         try {
           Thread.sleep(1000L);
         } catch (InterruptedException e) {
           logger.error("停止队列被中断", e);
         } 
       } 
     } 
   }
 
 
 
   
   public void flush() {
     run();
   }
   
   public Date getNextTime() {
     return this.nextTime;
   }
   
   public int getError() {
     return this.error.get();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\TimingConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */