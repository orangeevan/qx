package com.haipaite.common.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
  String name();
  
  String value();
  
  ValueType type() default ValueType.EXPRESSION;
  
  String defaultValue() default "";
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\Scheduled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */