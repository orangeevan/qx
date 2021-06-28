 package com.haipaite.common.resource.support;
 
 import org.apache.commons.lang.StringUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;
 import org.springframework.beans.BeansException;
 import org.springframework.context.ApplicationContext;
 import org.springframework.context.ApplicationContextAware;
 import org.springframework.core.convert.converter.Converter;
 
 
 
 
 
 public class StringToClassConverter
   implements Converter<String, Class>, ApplicationContextAware
 {
   private static final Logger logger = LoggerFactory.getLogger(StringToClassConverter.class);
   private ApplicationContext applicationContext;
   
   public Class convert(String source) {
     if (!StringUtils.contains(source, ".") && !source.startsWith("[")) {
       source = "java.lang." + source;
     }
     ClassLoader loader = this.applicationContext.getClassLoader();
     try {
       return Class.forName(source, true, loader);
     } catch (ClassNotFoundException e) {
       FormattingTuple message = MessageFormatter.format("无法将字符串[{}]转换为 Class", source);
       logger.error(message.getMessage());
       throw new IllegalArgumentException(message.getMessage());
     } 
   }
 
 
 
   
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
     this.applicationContext = applicationContext;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\support\StringToClassConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */