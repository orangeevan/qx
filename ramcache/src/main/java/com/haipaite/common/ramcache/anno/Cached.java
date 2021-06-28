package com.haipaite.common.ramcache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Cached {
  String size() default "maxOnline";
  
  int initialCapacity() default 16;
  
  int concurrencyLevel() default 16;
  
  Persister persister() default @Persister("30s");
  
  CacheType type() default CacheType.LRU;
  
  CacheUnit unit() default CacheUnit.ENTITY;
  
  boolean enhanced() default false;
  
  boolean caffeine() default true;
  
  int expireMinute() default 5;
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\anno\Cached.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */