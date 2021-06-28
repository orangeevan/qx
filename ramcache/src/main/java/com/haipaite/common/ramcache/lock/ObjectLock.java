 package com.haipaite.common.ramcache.lock;

 import com.haipaite.common.ramcache.IEntity;
 import java.util.concurrent.locks.ReentrantLock;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
















 public class ObjectLock
   extends ReentrantLock
   implements Comparable<ObjectLock>
 {
   private static final long serialVersionUID = -1738309259140428174L;
   private static final Class<IEntity> IENTITY_CLASS = IEntity.class;

   private static final Logger log = LoggerFactory.getLogger(ObjectLock.class);


   private final Class clz;


   private final Comparable value;


   private final boolean entity;



   public ObjectLock(Object object) {
     this(object, false);
   }






   public ObjectLock(Object object, boolean fair) {
     super(fair);
     this.clz = object.getClass();
     if (object instanceof IEntity) {
       this.value = (Comparable)((IEntity)object).getId();
     } else {
       this.value = new Integer(System.identityHashCode(object));
     }
     if (IENTITY_CLASS.isAssignableFrom(this.clz)) {
       this.entity = true;
     } else {
       this.entity = false;
     }
   }






   public boolean isTie(ObjectLock other) {
     if (this.clz != other.clz) {
       return false;
     }
     if (this.value.compareTo(other.value) == 0) {
       return true;
     }
     return false;
   }







   public Class getClz() {
     return this.clz;
   }





   public Comparable getValue() {
     return this.value;
   }





   public boolean isEntity() {
     return this.entity;
   }



   public int compareTo(ObjectLock o) {
     if (isEntity() && !o.isEntity())
       return 1;
     if (!isEntity() && o.isEntity()) {
       return -1;
     }

     if (this.clz != o.clz) {

       if (this.clz.hashCode() < o.clz.hashCode())
         return -1;
       if (this.clz.hashCode() > o.clz.hashCode()) {
         return 1;
       }
       return this.clz.getName().compareTo(o.clz.getName());
     }

     return this.value.compareTo(o.value);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\lock\ObjectLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */