 package com.haipaite.server;

 import com.haipaite.core.SessionManager;
 import com.haipaite.core.Wsession;
 import io.netty.channel.ChannelHandler.Sharable;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.channel.ChannelInboundHandlerAdapter;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;









 @Sharable
 @Component
 public class SessionHandler
   extends ChannelInboundHandlerAdapter
 {
   @Autowired
   private SessionManager sessionManager;

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
     Wsession session = Wsession.valueOf(ctx.channel());
     this.sessionManager.add(session);
     ctx.fireChannelActive();
   }


   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
     this.sessionManager.remove(ctx.channel().id());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\server\SessionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */