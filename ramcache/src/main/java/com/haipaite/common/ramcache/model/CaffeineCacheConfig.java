 package com.haipaite.common.ramcache.model;
 
 import org.springframework.cache.CacheManager;
 import org.springframework.cache.annotation.CachingConfigurerSupport;
 import org.springframework.cache.annotation.EnableCaching;
 import org.springframework.cache.caffeine.CaffeineCacheManager;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 
 
 
 
 
 
 
 @EnableCaching
 @Configuration
 public class CaffeineCacheConfig
   extends CachingConfigurerSupport
 {
   @Bean
   public CaffeineCacheManager cacheManager() {
     CaffeineCacheManager cacheManager = new CaffeineCacheManager();
 
 
 
 
 
 
 
 
 
 
 
     
     return cacheManager;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\model\CaffeineCacheConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */