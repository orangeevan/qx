package com.haipaite.common.ramcache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InitialConfig {
  InitialType type() default InitialType.ALL;
  
  String query() default "";
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\anno\InitialConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */