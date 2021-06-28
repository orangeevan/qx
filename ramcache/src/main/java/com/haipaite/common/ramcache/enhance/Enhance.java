package com.haipaite.common.ramcache.enhance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Enhance {
  String value() default "";
  
  Class<?> ignore() default void.class;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\enhance\Enhance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */