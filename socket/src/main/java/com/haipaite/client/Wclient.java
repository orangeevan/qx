 package com.haipaite.client;
 
 import io.netty.bootstrap.Bootstrap;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelFuture;
 import io.netty.channel.ChannelHandler;
 import io.netty.channel.ChannelInitializer;
 import io.netty.channel.ChannelOption;
 import io.netty.channel.EventLoopGroup;
 import io.netty.channel.nio.NioEventLoopGroup;
 import io.netty.channel.socket.SocketChannel;
 import io.netty.channel.socket.nio.NioSocketChannel;
 import io.netty.util.internal.StringUtil;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 public class Wclient
 {
   private static EventLoopGroup eventLoopGroup = (EventLoopGroup)new NioEventLoopGroup();
   
   private static Bootstrap b = new Bootstrap();
   
   static {
     b = (Bootstrap)((Bootstrap)b.group(eventLoopGroup)).channel(NioSocketChannel.class);
   }
   
   private List<ChannelHandler> extraHandlers = new ArrayList<>();
   
   public void addChannelHandler(ChannelHandler ch) {
     this.extraHandlers.add(ch);
   }
 
   
   public ChannelFuture connect(int port, String host) throws InterruptedException {
     ((Bootstrap)b.remoteAddress(host, port).option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true)))
       .handler((ChannelHandler)new ChannelInitializer<SocketChannel>()
         {
           protected void initChannel(SocketChannel sc) throws Exception {
             sc.pipeline().addLast("decoder", (ChannelHandler)new WclientPacketDecoder());
             sc.pipeline().addLast("encoder", (ChannelHandler)new WclientPacketEncoder());
             for (ChannelHandler ch : Wclient.this.extraHandlers) {
               sc.pipeline().addLast(new ChannelHandler[] { ch });
             } 
           }
         });
     return b.connect(host, port).sync();
   }
   
   public void shutdownGracefully() {
     eventLoopGroup.shutdownGracefully();
   }
   
   public static void main(String[] args) {
     System.out.println(StringUtil.toHexString(new byte[] { 5, 2, 3, 3, 4 }));
     System.out.println(StringUtil.byteToHexString(15));
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\client\Wclient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */