 package com.haipaite.common.ramcache.persist;
 
 import com.haipaite.common.ramcache.IEntity;
 import com.haipaite.common.ramcache.exception.ConfigurationException;
 import com.haipaite.common.ramcache.exception.StateException;
 import com.haipaite.common.ramcache.orm.Accessor;
 import com.haipaite.common.utility.DateUtils;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;
 import org.springframework.util.Assert;
 
 
 
 
 
 
 
 
 
 public class TimingPersister
   implements Persister
 {
   private static final Logger logger = LoggerFactory.getLogger(TimingPersister.class);
 
   
   private ConcurrentHashMap<String, Element> elements = new ConcurrentHashMap<>();
   
   private ConcurrentHashMap<Class<? extends IEntity>, Listener> listeners = new ConcurrentHashMap<>();
 
 
   
   private boolean initialize;
 
   
   private TimingConsumer consumer;
 
 
   
   public synchronized void initialize(String name, Accessor accessor, String period) {
     if (this.initialize) {
       throw new ConfigurationException("重复初始化异常");
     }
     Assert.notNull(accessor, "持久层数据访问器不能为 null");
     try {
       this.elements = new ConcurrentHashMap<>();
       this.consumer = new TimingConsumer(name, period, accessor, this);
       this.initialize = true;
     } catch (Exception e) {
       throw new ConfigurationException("定时持久化处理器[" + name + "]初始化失败:" + e.getMessage());
     } 
   }
 
 
   
   public void addListener(Class<? extends IEntity> clz, Listener listener) {
     if (listener == null) {
       throw new ConfigurationException("被添加的监听器实例不能为空");
     }
     this.listeners.put(clz, listener);
   }
 
   
   public Listener getListener(Class<? extends IEntity> clz) {
     return this.listeners.get(clz);
   }
 
 
 
 
 
 
 
   
   public void put(Element element) {
     if (element == null) {
       return;
     }
     if (this.stop) {
       FormattingTuple message = MessageFormatter.format("实体更新队列已经停止,更新元素[{}]将不被接受", element);
       logger.error(message.getMessage());
       throw new StateException(message.getMessage());
     } 
     
     String id = element.getIdentity();
     this.rwLock.readLock().lock();
     Lock lock = lockIdLock(id);
     try {
       Element prev = this.elements.get(id);
 
       
       if (prev == null) {
         this.elements.put(id, element);
         
         return;
       } 
       
       EventType prevType = prev.getType();
       if (!prev.update(element)) {
         this.elements.remove(id);
       
       }
       else if (prevType == EventType.DELETE && prev.getType() == EventType.UPDATE) {
         Listener listener = getListener(element.getEntityClass());
         if (listener != null) {
           listener.notify(EventType.DELETE, true, prev.getId(), null, null);
         }
       } 
     } finally {
       
       releaseIdLock(id, lock);
       this.rwLock.readLock().unlock();
     } 
   }
 
   
   public Map<String, String> getInfo() {
     HashMap<String, String> result = new HashMap<>();
     result.put("size", Integer.toString(size()));
     result.put("state", this.consumer.getState().name());
     result.put("nextTime", DateUtils.date2String(this.consumer.getNextTime(), "yyyy-MM-dd HH:mm:ss"));
     return result;
   }
 
 
   
   private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
   
   Collection<Element> clearElements() {
     this.rwLock.writeLock().lock();
     try {
       ArrayList<Element> result = new ArrayList<>(this.elements.values());
       this.elements.clear();
       return result;
     } finally {
       this.rwLock.writeLock().unlock();
     } 
   }
 
 
   
   private volatile boolean stop;
 
   
   public int size() {
     return this.elements.size();
   }
 
 
 
 
   
   public void shutdown() {
     this.stop = true;
     this.consumer.stop();
     while (this.consumer.getState() != TimingConsumerState.STOPPED) {
       Thread.yield();
     }
   }
   
   public void flush() {
     this.consumer.flush();
   }
   
   public TimingConsumer getConsumer() {
     return this.consumer;
   }
 
 
 
   
   private ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
 
 
   
   private Lock lockIdLock(String id) {
     ReentrantLock lock = this.locks.get(id);
     if (lock == null) {
       lock = new ReentrantLock();
       ReentrantLock prevLock = this.locks.putIfAbsent(id, lock);
       lock = (prevLock != null) ? prevLock : lock;
     } 
     lock.lock();
     return lock;
   }
 
   
   private void releaseIdLock(String id, Lock lock) {
     lock.unlock();
     this.locks.remove(id);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\TimingPersister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */