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
 import io.netty.handler.codec.http.HttpObjectAggregator;
 import io.netty.handler.codec.http.HttpRequestEncoder;
 import io.netty.handler.codec.http.HttpResponseDecoder;
 import java.util.ArrayList;
 import java.util.List;









 public class WhttpClient
 {
   private static EventLoopGroup eventLoopGroup = (EventLoopGroup)new NioEventLoopGroup();
   private static Bootstrap b = new Bootstrap();

   private Channel channel;

   private List<ChannelHandler> extraHandlers = new ArrayList<>();

   public void addChannelHandler(ChannelHandler ch) {
     this.extraHandlers.add(ch);
   }

   public ChannelFuture connect(int port, String host) throws InterruptedException {
     ((Bootstrap)((Bootstrap)((Bootstrap)b.group(eventLoopGroup)).channel(NioSocketChannel.class)).option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true)))
       .handler((ChannelHandler)new ChannelInitializer<SocketChannel>()
         {
           protected void initChannel(SocketChannel sc) throws Exception {
             sc.pipeline().addLast("decoder", (ChannelHandler)new HttpResponseDecoder());
             sc.pipeline().addLast("http-aggregator", (ChannelHandler)new HttpObjectAggregator(524288));
             sc.pipeline().addLast("encoder", (ChannelHandler)new HttpRequestEncoder());
             for (ChannelHandler ch : WhttpClient.this.extraHandlers) {
               sc.pipeline().addLast(new ChannelHandler[] { ch });
             }
           }
         });
     ChannelFuture channelFuture = b.connect(host, port).sync();
     this.channel = channelFuture.channel();
     return channelFuture;
   }

   public void sendPacket(Object packet) {
     this.channel.writeAndFlush(packet);
   }





   public void shutdown() {
     try {
       this.channel.close();
     } finally {
       eventLoopGroup.shutdownGracefully();
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\client\WhttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */