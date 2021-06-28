 package com.haipaite.common.ramcache.util;

 import com.haipaite.common.ramcache.IEntity;
 import com.haipaite.common.ramcache.entity.User;
 import com.haipaite.common.ramcache.orm.Accessor;
 import com.haipaite.common.ramcache.orm.Querier;
 import com.haipaite.common.ramcache.persist.Element;
 import com.haipaite.common.ramcache.persist.EventType;
 import com.haipaite.common.ramcache.persist.Listener;
 import com.haipaite.common.ramcache.persist.TimingPersister;
 import java.io.Serializable;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;










 @Component
 public class JDBCOp
 {
   @Autowired
   Accessor accessor;
   @Autowired
   Querier querier;

   public void operate() {
     TimingPersister queue = new TimingPersister();
     class TestListener
       implements Listener {
       EventType type;
       Serializable id;
       IEntity entity;
       boolean success;

       public void notify(EventType type, boolean isSuccess, Serializable id, IEntity entity, RuntimeException ex) {
         this.success = isSuccess;
         this.type = type;
         this.id = id;
         this.entity = entity;
       }
     };
     TestListener listener = new TestListener();
     queue.addListener(User.class, listener);
     queue.initialize("test_listener", this.accessor, "10");


     User entity = new User();
     entity.setId(1L);
     entity.setAge(Integer.valueOf(1));
     entity.setUser_name("大白");
     entity.setNice_name("大白");
     queue.put(Element.saveOf((IEntity)entity));
     try {
       Thread.currentThread(); Thread.sleep(10000L);
     } catch (Exception exception) {}




     entity.setUser_name(entity.getUser_name() + " Edit");
     queue.put(Element.updateOf((IEntity)entity));
     try {
       Thread.currentThread(); Thread.sleep(10000L);
     } catch (Exception exception) {}
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcach\\util\JDBCOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */