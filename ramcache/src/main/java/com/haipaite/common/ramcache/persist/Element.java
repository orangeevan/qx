 package com.haipaite.common.ramcache.persist;
 
 import com.haipaite.common.ramcache.IEntity;
 import java.io.Serializable;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 
 
 public class Element
 {
   private static final Logger logger = LoggerFactory.getLogger(Element.class);
   
   private EventType type;
   private final Serializable id;
   
   public static Element saveOf(IEntity entity) {
     return new Element(EventType.INSERT, entity.getId(), entity, (Class)entity
         .getClass());
   }
 
   
   private IEntity entity;
   private final Class<? extends IEntity> entityClass;
   
   public static Element removeOf(Serializable id, Class<? extends IEntity> entityClass) {
     return new Element(EventType.DELETE, id, null, entityClass);
   }
 
 
 
   
   public static Element updateOf(IEntity entity) {
     return new Element(EventType.UPDATE, entity.getId(), entity, (Class)entity
         .getClass());
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   private Element(EventType type, Serializable id, IEntity entity, Class<? extends IEntity> entityClass) {
     this.type = type;
     this.id = id;
     this.entity = entity;
     this.entityClass = entityClass;
   }
 
 
 
   
   public String getIdentity() {
     return this.entityClass.getName() + ":" + this.id;
   }
 
 
 
 
 
 
 
   
   public boolean update(Element element) {
     this.entity = element.getEntity();
     switch (this.type) {
       
       case INSERT:
         switch (element.getType()) {
           
           case INSERT:
             logger.error("更新元素异常，实体[{}]原状态[{}]当前状态[{}]不进行修正", new Object[] {
                   getIdentity(), this.type, element.getType() });
             break;
           case UPDATE:
             if (logger.isDebugEnabled())
               logger.debug("实体[{}]原状态[{}]当前状态[{}]修正后状态[{}]是否保留队列元素[{}]", new Object[] {
                     getIdentity(), EventType.INSERT, element
                     .getType(), this.type, Boolean.valueOf(true)
                   }); 
             break;
           case DELETE:
             if (logger.isDebugEnabled())
               logger.debug("实体[{}]原状态[{}]当前状态[{}]修正后状态[{}]是否保留队列元素[{}]", new Object[] {
                     getIdentity(), EventType.INSERT, element
                     .getType(), this.type, Boolean.valueOf(false)
                   }); 
             return false;
         } 
         
         break;
       case UPDATE:
         switch (element.getType()) {
           case INSERT:
             logger.error("更新元素异常，实体[{}]原状态[{}]当前状态[{}]不进行修正", new Object[] {
                   getIdentity(), this.type, element.getType() });
             break;
           case UPDATE:
             if (logger.isDebugEnabled())
               logger.debug("实体[{}]原状态[{}]当前状态[{}]修正后状态[{}]是否保留队列元素[{}]", new Object[] {
                     getIdentity(), EventType.INSERT, element
                     .getType(), this.type, Boolean.valueOf(true)
                   }); 
             break;
           case DELETE:
             this.type = EventType.DELETE;
             if (logger.isDebugEnabled()) {
               logger.debug("实体[{}]原状态[{}]当前状态[{}]修正后状态[{}]是否保留队列元素[{}]", new Object[] {
                     getIdentity(), EventType.INSERT, element
                     .getType(), this.type, Boolean.valueOf(true)
                   });
             }
             break;
         } 
         break;
       case DELETE:
         switch (element.getType()) {
           case INSERT:
             this.type = EventType.UPDATE;
             if (logger.isDebugEnabled())
               logger.debug("实体[{}]原状态[{}]当前状态[{}]修正后状态[{}]是否保留队列元素[{}]", new Object[] {
                     getIdentity(), EventType.DELETE, EventType.INSERT, this.type, 
                     Boolean.valueOf(true)
                   }); 
             break;
           case UPDATE:
             logger.error("更新元素异常，实体[{}]原状态[{}]当前状态[{}]不进行修正", new Object[] {
                   getIdentity(), this.type, element.getType() });
             break;
           case DELETE:
             logger.error("更新元素异常，实体[{}]原状态[{}]当前状态[{}]不进行修正", new Object[] {
                   getIdentity(), this.type, element.getType() });
             break;
         } 
         break;
     } 
     return true;
   }
 
 
   
   public EventType getType() {
     return this.type;
   }
   
   public Serializable getId() {
     return this.id;
   }
   
   public IEntity getEntity() {
     return this.entity;
   }
   
   public Class<? extends IEntity> getEntityClass() {
     return this.entityClass;
   }
 
   
   public String toString() {
     StringBuilder builder = new StringBuilder();
     builder.append(this.type).append(" ID:").append(this.id);
     return builder.toString();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */