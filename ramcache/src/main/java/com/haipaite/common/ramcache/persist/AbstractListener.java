 package com.haipaite.common.ramcache.persist;
 
 import com.haipaite.common.ramcache.IEntity;
 import java.io.Serializable;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 public abstract class AbstractListener
   implements Listener
 {
   private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);
 
   
   public void notify(EventType type, boolean isSuccess, Serializable id, IEntity entity, RuntimeException ex) {
     try {
       if (isSuccess) {
         switch (type) {
           case INSERT:
             onSaveSuccess(id, entity);
             return;
           case UPDATE:
             onUpdateSuccess(entity);
             return;
           case DELETE:
             onRemoveSuccess(id);
             return;
         } 
         logger.error("未支持的更新事件类型[{}]", type);
       }
       else {
         
         switch (type) {
           case INSERT:
             onSaveError(id, entity, ex);
             return;
           case UPDATE:
             onUpdateError(entity, ex);
             return;
           case DELETE:
             onRemoveError(id, ex);
             return;
         } 
         logger.error("未支持的更新事件类型[{}]", type);
       }
     
     }
     catch (Exception e) {
       logger.error("队列监听器[{}]处理出现异常", new Object[] { getClass().getName(), e });
     } 
   }
   
   protected void onSaveSuccess(Serializable id, IEntity entity) {}
   
   protected void onUpdateSuccess(IEntity entity) {}
   
   protected void onRemoveSuccess(Serializable id) {}
   
   protected void onSaveError(Serializable id, IEntity entity, RuntimeException ex) {}
   
   protected void onUpdateError(IEntity entity, RuntimeException ex) {}
   
   protected void onRemoveError(Serializable id, RuntimeException ex) {}
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\AbstractListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */