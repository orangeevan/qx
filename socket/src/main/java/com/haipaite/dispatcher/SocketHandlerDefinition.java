 package com.haipaite.dispatcher;

 import com.baidu.bjf.remoting.protobuf.Codec;
 import com.haipaite.core.Wsession;
 import java.lang.reflect.Method;
 import org.springframework.util.ReflectionUtils;










 public class SocketHandlerDefinition
 {
   private Object bean;
   private Method method;
   private Codec codec;

   public static SocketHandlerDefinition valueOf(Object bean, Method method, Codec codec) {
     SocketHandlerDefinition shd = new SocketHandlerDefinition();
     shd.bean = bean;
     shd.method = method;
     shd.codec = codec;
     return shd;
   }

   public Object invoke(Wsession session, Object packet) {
     return ReflectionUtils.invokeMethod(this.method, this.bean, new Object[] { session, packet });
   }

   public Object getBean() {
     return this.bean;
   }

   public Method getMethod() {
     return this.method;
   }

   public Codec getCodec() {
     return this.codec;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\dispatcher\SocketHandlerDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */