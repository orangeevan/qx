 package com.haipaite.common.utility.chain.impl;

 import com.haipaite.common.utility.chain.NodeProcessor;
 import com.haipaite.common.utility.chain.anno.InNotice;
 import com.haipaite.common.utility.chain.anno.Processing;
 import com.haipaite.common.utility.chain.anno.Type;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.Method;
 import org.springframework.core.BridgeMethodResolver;
 import org.springframework.core.annotation.AnnotationUtils;












 public abstract class AnnotationMethodNodeBuilder
 {
   public static boolean isValid(Method method) {
     Processing anno = (Processing)AnnotationUtils.getAnnotation(method, Processing.class);
     if (anno != null) {
       return true;
     }
     return false;
   }







   public static NodeProcessor build(Object target, Method method) {
     method = BridgeMethodResolver.findBridgedMethod(method);
     Processing anno = method.<Processing>getAnnotation(Processing.class);
     Type[] types = getInjectTypes(method);
     return new AnnotationMethodProcessor(target, method, anno, types);
   }






   private static Type[] getInjectTypes(Method method) {
     Class<?>[] classes = method.getParameterTypes();
     Annotation[][] annotations = method.getParameterAnnotations();
     Type[] result = new Type[classes.length];
     for (int i = 0; i < classes.length; i++) {
       InNotice inNotice = getInNotice(annotations[i]);
       if (inNotice != null) {
         result[i] = inNotice.type();
       } else {
         result[i] = Type.NOTICE;
       }
     }
     return result;
   }






   private static InNotice getInNotice(Annotation[] annotations) {
     for (Annotation a : annotations) {
       if (a instanceof InNotice) {
         return (InNotice)a;
       }
     }
     return null;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\impl\AnnotationMethodNodeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */