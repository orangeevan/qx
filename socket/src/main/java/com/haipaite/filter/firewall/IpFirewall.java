 package com.haipaite.filter.firewall;

 import com.haipaite.core.SessionManager;
 import com.haipaite.core.Wsession;
 import com.haipaite.utils.IpUtils;
 import io.netty.channel.ChannelHandler.Sharable;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.channel.ChannelInboundHandlerAdapter;
 import java.util.HashSet;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;









 @Sharable
 @Component
 public class IpFirewall
   extends ChannelInboundHandlerAdapter
 {
   private static final Logger logger = LoggerFactory.getLogger(IpFirewall.class);




   private HashSet<String> allows = new HashSet<>();



   private HashSet<String> blocks = new HashSet<>();



   private HashSet<String> managements = new HashSet<>();



   @Autowired
   private SessionManager sessionManager;


   private final int MAX_IP_SESSION_COUNT = Integer.MAX_VALUE;



   private volatile boolean firewallOpen = false;


   private volatile boolean opened = false;



   public void open() {
     this.opened = true;
   }

   public void close() {
     this.opened = false;
   }


   public void channelActive(ChannelHandlerContext ctx) throws Exception {
     if (!this.opened) {
       return;
     }

     if (this.firewallOpen &&
       !this.allows.contains(IpUtils.getIp(ctx.channel().remoteAddress().toString()))) {
       ctx.channel().close();
       logger.info(String.format("防火墙未开启,阻止IP[%s]连接.", new Object[] { ctx.channel().remoteAddress() }));

       return;
     }
     if (this.blocks.contains(IpUtils.getIp(ctx.channel().remoteAddress().toString()))) {
       ctx.channel().close();
       logger.info(String.format("IP[%s]在黑名单中,断开连接.", new Object[] { ctx.channel().remoteAddress() }));
       return;
     }
     int ipCount = this.sessionManager.ipSessionCount(IpUtils.getIp(ctx.channel().remoteAddress()));
     if (Integer.MAX_VALUE <= ipCount) {
       ctx.channel().close();
       logger.info(String.format("超过最大连接数[%s],IP[%s]连接数[%s].", new Object[] { Integer.valueOf(2147483647), ctx.channel().remoteAddress(),
               Integer.valueOf(ipCount) }));
       return;
     }
     ctx.fireChannelActive();
   }







   public void addBlocks(Wsession session, String... ips) {
     if (ips != null) {
       for (String ip : ips) {
         if (!IpUtils.isIpv4(ip)) {
           throw new RuntimeException(
               String.format("管理后台IP[%s]添加IP[%s]格式非法.", new Object[] { session.getChannel().remoteAddress(), ip }));
         }
       }
       for (String ip : ips) {
         this.blocks.add(ip);
         logger.info(String.format("管理后台IP[%s]添加IP[%s]到黑名单.", new Object[] { session.getChannel().remoteAddress(), ip }));
       }
     }
   }







   public void addAllows(Wsession session, String... ips) {
     if (ips != null) {
       for (String ip : ips) {
         if (!IpUtils.isIpv4(ip)) {
           throw new RuntimeException(
               String.format("管理后台IP[%s]添加IP[%s]格式非法.", new Object[] { session.getChannel().remoteAddress(), ip }));
         }
       }
       for (String ip : ips) {
         this.allows.add(ip);
         logger.info(String.format("管理后台IP[%s]添加IP[%s]到白名单.", new Object[] { session.getChannel().remoteAddress(), ip }));
       }
     }
   }







   public void addManagements(Wsession session, String... ips) {
     if (ips != null) {
       for (String ip : ips) {
         if (!IpUtils.isIpv4(ip)) {
           throw new RuntimeException(
               String.format("管理后台IP[%s]添加IP[%s]格式非法.", new Object[] { session.getChannel().remoteAddress(), ip }));
         }
       }
       for (String ip : ips) {
         this.managements.add(ip);
         logger.info(String.format("管理后台IP[%s]添加IP[%s]到管理后台.", new Object[] { session.getChannel().remoteAddress(), ip }));

         addAllows(session, new String[] { ip });
       }
     }
   }





   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     if (this.blocks.contains(IpUtils.getIp(ctx.channel().remoteAddress().toString()))) {
       ctx.channel().close();
       logger.info(String.format("IP[%s]在黑名单中禁止通信,断开连接.", new Object[] { ctx.channel().remoteAddress() }));
       return;
     }
     ctx.fireChannelRead(msg);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\filter\firewall\IpFirewall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */