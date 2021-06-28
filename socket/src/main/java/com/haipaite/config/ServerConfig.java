 package com.haipaite.config;

 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Properties;
 import javax.annotation.PostConstruct;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;










 @Component
 public class ServerConfig
 {
   private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);
   private Properties prop = new Properties();

   @PostConstruct
   public void initProperties() throws IOException {
     String fileName = "wnet.properties";

     InputStream inputStream = ServerConfig.class.getClassLoader().getResourceAsStream(fileName);
     if (inputStream == null) {
       logger.error(String.format("没有找到wnet.properties文件!", new Object[0]));
       throw new RuntimeException("没有找到wnet.properties文件!");
     }
     this.prop.load(inputStream);
   }

   public String getProp(String key) {
     return this.prop.getProperty(key);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\config\ServerConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */