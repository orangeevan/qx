 package com.haipaite.common.ramcache.persist;
 
 import com.haipaite.common.ramcache.IEntity;
 import com.haipaite.common.ramcache.exception.ConfigurationException;
 import com.haipaite.common.ramcache.exception.StateException;
 import com.haipaite.common.ramcache.orm.Accessor;
 import com.haipaite.common.utility.collection.ConcurrentHashSet;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.LinkedBlockingQueue;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;
 import org.springframework.util.Assert;
 
 
 
 
 
 
 
 
 public class QueuePersister
   implements Persister
 {
   private static final Logger logger = LoggerFactory.getLogger(QueuePersister.class);
 
   
   private static final String SPLIT = ":";
   
   private String name;
   
   private BlockingQueue<Element> queue;
   
   private ConcurrentHashMap<Class<? extends IEntity>, Listener> listeners = new ConcurrentHashMap<>();
   
   private boolean initialize;
   
   private boolean flag;
   
   private ConcurrentHashSet<String> updating = new ConcurrentHashSet();
   
   private QueueConsumer consumer;
   
   private volatile boolean stop;
 
   
   public synchronized void initialize(String name, Accessor accessor, String config) {
     if (this.initialize) {
       throw new ConfigurationException("重复初始化异常");
     }
     Assert.notNull(accessor, "持久层数据访问器不能为 null");
     
     try {
       String[] array = config.split(":");
       int size = Integer.parseInt(array[0]);
       if (size >= 0) {
         this.queue = new ArrayBlockingQueue<>(size);
       } else {
         this.queue = new LinkedBlockingQueue<>();
       } 
       
       this.flag = Boolean.parseBoolean(array[1]);
       this.name = name;
       this.consumer = new QueueConsumer(name, accessor, this.queue, this);
       this.initialize = true;
     } catch (Exception e) {
       throw new ConfigurationException("持久化处理器[" + name + "]初始化异常:" + e.getMessage());
     } 
   }
 
   
   public void addListener(Class<? extends IEntity> clz, Listener listener) {
     if (listener == null) {
       throw new ConfigurationException("被添加的监听器实例不能为空");
     }
     this.listeners.put(clz, listener);
   }
 
 
 
 
   
   public void put(Element element) {
     if (element == null) {
       return;
     }
     if (this.stop) {
       FormattingTuple message = MessageFormatter.format("实体更新队列[{}]已经停止,更新元素[{}]将不被接受", this.name, element);
       logger.error(message.getMessage());
       throw new StateException(message.getMessage());
     } 
     
     try {
       if (this.flag && element.getType() == EventType.UPDATE) {
         
         String identity = element.getIdentity();
         if (this.updating.contains(identity)) {
           return;
         }
         this.updating.add(identity);
       } 
       this.queue.put(element);
     } catch (InterruptedException e) {
       
       logger.error("等待将元素[{}]添加到队列时被打断", new Object[] { element, e });
       if (element.getType() == EventType.UPDATE) {
         this.updating.remove(element.getIdentity());
       }
     } 
   }
 
   
   public Listener getListener(Class<? extends IEntity> clz) {
     return this.listeners.get(clz);
   }
 
   
   public Map<String, String> getInfo() {
     HashMap<String, String> result = new HashMap<>();
     result.put("size", Integer.toString(size()));
     result.put("error", Integer.toString(this.consumer.getError()));
     return result;
   }
   
   public void removeUpdating(String identity) {
     if (this.flag) {
       this.updating.remove(identity);
     }
   }
 
 
 
 
   
   public int size() {
     return this.queue.size();
   }
 
 
 
 
   
   public void shutdown() {
     this.stop = true;
     
     while (!this.queue.isEmpty())
     {
       
       Thread.yield();
     }
     if (logger.isDebugEnabled())
       logger.debug("实体更新队列[{}]完成关闭", this.name); 
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\QueuePersister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */