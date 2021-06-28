 package com.haipaite.common.event.core;
 
 import com.haipaite.common.event.anno.ReceiverAnno;
 import com.haipaite.common.event.event.IEvent;
 import com.haipaite.common.event.util.EnhanceUtil;
 import com.haipaite.common.utility.ThreadUtils;
 import java.lang.reflect.Method;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.TimeUnit;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class EventBusManager implements IEventBusManager {
   private static final Logger logger = LoggerFactory.getLogger(EventBusManager.class);
   
   private static final int EXECUOTRS_SIZE = Runtime.getRuntime().availableProcessors();
   
   private ExecutorService[] executors;
   
   private Map<Class<? extends IEvent>, List<IReceiverInvoke>> ReceiverDefintionMap = new HashMap<>();
   
   private static IEventBusManager self;
   
   public EventBusManager() {
     init();
   }
   
   private synchronized void init() {
     this.executors = new ExecutorService[EXECUOTRS_SIZE];
     for (int i = 0; i < EXECUOTRS_SIZE; i++) {
       this.executors[i] = Executors.newSingleThreadExecutor();
     }
     self = this;
   }
 
   
   public static IEventBusManager getInstance() {
     return self;
   }
   
   public void syncSubmit(IEvent event) {
     doSubmitEvent(event);
   }
   
   public void submit(IEvent event) {
     this.executors[Math.abs((int)(event.getOwner() % EXECUOTRS_SIZE))].submit(() -> doSubmitEvent(event));
   }
 
 
   
   protected void doSubmitEvent(IEvent event) {
     try {
       List<IReceiverInvoke> defintions = getReceiversByEvent(event);
       if (defintions != null) {
         defintions.forEach(defintion -> {
               try {
                 defintion.invoke(event);
               } catch (Exception e) {
                 logger.error("事件处理异常", e);
               } 
             });
       }
     } catch (Exception e) {
       logger.error("事件处理异常", e);
     } 
   }
   
   private List<IReceiverInvoke> getReceiversByEvent(IEvent event) {
     Class<?> clz = event.getClass();
     List<IReceiverInvoke> temp = this.ReceiverDefintionMap.get(clz);
     if (temp == null || temp.isEmpty()) {
       logger.warn("no any receivers found for event : " + event.getClass());
     }
     return temp;
   }
   
   public void registReceiver(Object bean) throws Exception {
     Class<?> clz = bean.getClass();
     Method[] methods = clz.getDeclaredMethods();
     for (Method method : methods) {
       if (method.isAnnotationPresent((Class)ReceiverAnno.class)) {
         ReceiverDefintion defintion = ReceiverDefintion.valueOf(bean, method);
         registDefintion(defintion.getClz(), EnhanceUtil.createReceiverInvoke(defintion));
       } 
     } 
   }
   
   public void registReceiver(Object bean, Method method, Class<? extends IEvent> clazz) throws Exception {
     registDefintion(clazz, EnhanceUtil.createReceiverInvoke(ReceiverDefintion.valueOf(bean, method, clazz)));
   }
   
   private void registDefintion(Class<? extends IEvent> clz, IReceiverInvoke defintion) {
     if (!this.ReceiverDefintionMap.containsKey(clz)) {
       this.ReceiverDefintionMap.put(clz, new CopyOnWriteArrayList<>());
     }
     if (!((List)this.ReceiverDefintionMap.get(clz)).contains(defintion)) {
       ((List<IReceiverInvoke>)this.ReceiverDefintionMap.get(clz)).add(defintion);
     }
   }
   
   public void shutdown() {
     if (Objects.nonNull(this.executors))
       Arrays.<ExecutorService>stream(this.executors).forEach(executorService -> ThreadUtils.shutdownGracefully(executorService, 1L, TimeUnit.SECONDS)); 
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\core\EventBusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */