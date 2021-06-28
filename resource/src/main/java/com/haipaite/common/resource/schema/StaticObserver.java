 package com.haipaite.common.resource.schema;

 import com.haipaite.common.resource.Storage;
 import com.haipaite.common.resource.anno.Static;
 import java.lang.reflect.Field;
 import java.util.Observable;
 import java.util.Observer;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;








 public class StaticObserver
   implements Observer
 {
   private static final Logger logger = LoggerFactory.getLogger(StaticObserver.class);
   private final Object target;
   private final Field field;

   public void update(Observable o, Object arg) {
     if (!(o instanceof Storage)) {
       if (logger.isDebugEnabled()) {
         FormattingTuple message = MessageFormatter.format("被观察对象[{}]不是指定类型", o.getClass());
         logger.debug(message.getMessage());
       }

       return;
     }
     inject((Storage)o);
   }

   private final Static anno;

   private void inject(Storage o) {
     Object value = o.get(this.key, false);
     if (this.anno.required() && value == null) {
       FormattingTuple message = MessageFormatter.format("被注入属性[{}]不存在[key:{}]", this.field, this.key);
       logger.error(message.getMessage());
       throw new RuntimeException(message.getMessage());
     }

     try {
       this.field.set(this.target, value);
     } catch (Exception e) {
       FormattingTuple message = MessageFormatter.format("无法设置被注入属性[{}]", this.field);
       logger.error(message.getMessage());
       throw new RuntimeException(message.getMessage());
     }
   }





   private final Object key;




   public StaticObserver(Object target, Field field, Static anno, Object key) {
     this.target = target;
     this.field = field;
     this.anno = anno;
     this.key = key;
   }



   public Object getTarget() {
     return this.target;
   }

   public Field getField() {
     return this.field;
   }

   public Static getAnno() {
     return this.anno;
   }

   public Object getKey() {
     return this.key;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\schema\StaticObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */