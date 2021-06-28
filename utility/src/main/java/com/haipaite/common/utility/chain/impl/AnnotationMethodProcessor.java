 package com.haipaite.common.utility.chain.impl;

 import com.haipaite.common.utility.chain.Notice;
 import com.haipaite.common.utility.chain.Way;
 import com.haipaite.common.utility.chain.anno.Processing;
 import com.haipaite.common.utility.chain.anno.Type;
 import java.lang.reflect.Method;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;








 public class AnnotationMethodProcessor
   extends AbstractNode
 {
   private static final Logger log = LoggerFactory.getLogger(AnnotationMethodProcessor.class);


   private final Object target;


   private final Method method;


   private final Type[] types;



   public AnnotationMethodProcessor(Object target, Method method, Processing anno, Type[] types) {
     super(anno.name(), anno.index(), anno.way());
     this.target = target;
     this.method = method;
     this.types = types;
     method.setAccessible(true);
   }


   public boolean in(Notice notice) {
     if (getWay() == Way.OUT) {
       return true;
     }
     return execute(notice);
   }


   public boolean out(Notice notice) {
     if (getWay() == Way.IN) {
       return true;
     }
     return execute(notice);
   }






   private boolean execute(Notice notice) {
     Object[] params = getParams(notice);
     try {
       Object result = this.method.invoke(this.target, params);
       if (result == null) {
         return true;
       }
       if (result instanceof Boolean) {
         return ((Boolean)result).booleanValue();
       }
     } catch (Exception e) {
       log.error("执行方法时出现异常", e);
       throw new IllegalStateException("无法执行处理方法", e);
     }
     return true;
   }






   private Object[] getParams(Notice notice) {
     Object[] result = new Object[this.types.length];
     for (int i = 0; i < this.types.length; i++) {
       Type type = this.types[i];
       result[i] = getParam(notice, type);
     }
     return result;
   }







   private Object getParam(Notice notice, Type type) {
     switch (type) {
       case NOTICE:
         return notice;
       case CONTENT:
         return notice.getContent();
       case STEP:
         return Integer.valueOf(notice.getStep());
       case WAY:
         return notice.getWay();
     }
     log.error("无法处理的参数类型[{}]", type);
     return null;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\impl\AnnotationMethodProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */