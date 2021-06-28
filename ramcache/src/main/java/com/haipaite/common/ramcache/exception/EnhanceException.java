 package com.haipaite.common.ramcache.exception;

 import com.haipaite.common.ramcache.IEntity;








 public class EnhanceException
   extends CacheException
 {
   private static final long serialVersionUID = -8348735808262762811L;
   private final IEntity entity;

   public EnhanceException(IEntity entity) {
     this.entity = entity;
   }

   public EnhanceException(IEntity entity, String message, Throwable cause) {
     super(message, cause);
     this.entity = entity;
   }

   public EnhanceException(IEntity entity, String message) {
     super(message);
     this.entity = entity;
   }

   public EnhanceException(IEntity entity, Throwable cause) {
     super(cause);
     this.entity = entity;
   }

   public IEntity getEntity() {
     return this.entity;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\exception\EnhanceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */