 package com.haipaite.common.event.core;

 import com.haipaite.common.event.event.IEvent;
 import java.lang.reflect.Method;
 import org.springframework.util.ReflectionUtils;

 public class ReceiverDefintion
   implements IReceiverInvoke
 {
   private final Object bean;
   private final Method method;
   private final Class<? extends IEvent> clz;

   private ReceiverDefintion(Object bean, Method method, Class<? extends IEvent> clz) {
     this.bean = bean;
     this.method = method;
     this.clz = clz;
   }

   public Class<? extends IEvent> getClz() {
     return this.clz;
   }

   public void invoke(IEvent event) {
     ReflectionUtils.makeAccessible(this.method);
     ReflectionUtils.invokeMethod(this.method, this.bean, new Object[] { event });
   }


   public static ReceiverDefintion valueOf(Object bean, Method method) {
     Class<? extends IEvent> clz = null;
     Class<?>[] clzs = method.getParameterTypes();
     if (clzs.length != 1) {
       throw new IllegalArgumentException("class" + bean.getClass().getSimpleName() + " method" + method.getName() + " must only has one parameter Exception");
     }

     if (!IEvent.class.isAssignableFrom(clzs[0])) {
       throw new IllegalArgumentException("class" + bean.getClass().getSimpleName() + " method" + method.getName() + " must only has one [IEvent] type parameter Exception");
     }

     clz = (Class)clzs[0];
     return new ReceiverDefintion(bean, method, clz);
   }

   public static ReceiverDefintion valueOf(Object bean, Method method, Class<? extends IEvent> clz) {
     return new ReceiverDefintion(bean, method, clz);
   }


   public int hashCode() {
     int prime = 31;
     int result = 1;
     result = 31 * result + ((this.bean == null) ? 0 : this.bean.hashCode());
     result = 31 * result + ((this.clz == null) ? 0 : this.clz.hashCode());
     result = 31 * result + ((this.method == null) ? 0 : this.method.hashCode());
     return result;
   }


   public boolean equals(Object obj) {
     if (this == obj)
       return true;
     if (obj == null)
       return false;
     if (getClass() != obj.getClass())
       return false;
     ReceiverDefintion other = (ReceiverDefintion)obj;
     if (this.bean == null) {
       if (other.bean != null)
         return false;
     } else if (!this.bean.equals(other.bean)) {
       return false;
     }  if (this.clz == null) {
       if (other.clz != null)
         return false;
     } else if (!this.clz.equals(other.clz)) {
       return false;
     }  if (this.method == null) {
       if (other.method != null)
         return false;
     } else if (!this.method.equals(other.method)) {
       return false;
     }  return true;
   }

   public Object getBean() {
     return this.bean;
   }

   public Method getMethod() {
     return this.method;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\core\ReceiverDefintion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */