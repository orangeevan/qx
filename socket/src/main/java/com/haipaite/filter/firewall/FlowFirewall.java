 package com.haipaite.filter.firewall;
 
 import com.haipaite.core.SessionManager;
 import com.haipaite.core.Wsession;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandler.Sharable;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.channel.ChannelInboundHandlerAdapter;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 
 
 
 
 
 
 
 
 @Sharable
 @Component
 public class FlowFirewall
   extends ChannelInboundHandlerAdapter
 {
   private static final Logger logger = LoggerFactory.getLogger(FlowFirewall.class);
   
   @Autowired
   private SessionManager sessionManager;
 
   
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     Wsession session = this.sessionManager.getSession(ctx.channel().id());
     if (session != null) {
       ByteBuf buf = (ByteBuf)msg;
       boolean outOfLine = session.getFirewallRecord().check(buf.readableBytes());
       if (outOfLine) {
         
         logger.error(String.format("id[%s],ip[%s]流量违规!record[%s]", new Object[] { session.getChannel().id(), session
                 .getChannel().remoteAddress(), session.getFirewallRecord() }));
         ctx.channel().close();
         
         return;
       } 
     } 
     ctx.fireChannelRead(msg);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\filter\firewall\FlowFirewall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */