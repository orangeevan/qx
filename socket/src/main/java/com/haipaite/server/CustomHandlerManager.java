 package com.haipaite.server;
 
 import io.netty.channel.ChannelHandlerAdapter;
 import java.util.ArrayList;
 import java.util.List;
 import javax.annotation.PostConstruct;
 import org.springframework.stereotype.Component;
 
 
 
 
 
 
 
 
 
 
 @Component
 public final class CustomHandlerManager
 {
   private List<ChannelHandlerAdapter> handlers = new ArrayList<>();
   
   @PostConstruct
   public void init() {
     doInit();
   }
 
   
   private void doInit() {}
 
   
   public List<ChannelHandlerAdapter> getHandlers() {
     return this.handlers;
   }
   
   public void setHandlers(List<ChannelHandlerAdapter> handlers) {
     this.handlers = handlers;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\server\CustomHandlerManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */