 package com.haipaite.server;
 
 import io.netty.bootstrap.ServerBootstrap;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelFuture;
 import io.netty.channel.ChannelHandler;
 import io.netty.channel.ChannelHandlerAdapter;
 import io.netty.channel.ChannelInitializer;
 import io.netty.channel.ChannelOption;
 import io.netty.channel.EventLoopGroup;
 import io.netty.channel.nio.NioEventLoopGroup;
 import io.netty.channel.socket.SocketChannel;
 import io.netty.channel.socket.nio.NioServerSocketChannel;
 import io.netty.handler.codec.http.HttpObjectAggregator;
 import io.netty.handler.codec.http.HttpRequestDecoder;
 import io.netty.handler.codec.http.HttpResponseEncoder;
 import java.net.InetSocketAddress;
 import java.util.ArrayList;
 import java.util.List;
 import javax.annotation.PreDestroy;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;
 
 
 @Component
 public class WhttpServer
 {
   private static final Logger logger = LoggerFactory.getLogger(WhttpServer.class);
   
   private List<ChannelHandlerAdapter> channeHandles = new ArrayList<>();
   
   public void bind(int port) throws InterruptedException {
     this.bossGroup = (EventLoopGroup)new NioEventLoopGroup();
     this.workerGroup = (EventLoopGroup)new NioEventLoopGroup();
     ServerBootstrap b = new ServerBootstrap();
     ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)b.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class))
       .option(ChannelOption.ALLOW_HALF_CLOSURE, Boolean.valueOf(false)))
       .option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true)))
       .childHandler((ChannelHandler)new ChannelInitializer<SocketChannel>()
         {
 
 
 
 
           
           protected void initChannel(SocketChannel ch) throws Exception
           {
             ch.pipeline().addLast("http-decoder", (ChannelHandler)new HttpRequestDecoder());
             ch.pipeline().addLast("http-aggregator", (ChannelHandler)new HttpObjectAggregator(524288));
             ch.pipeline().addLast("http-encoder", (ChannelHandler)new HttpResponseEncoder());
             for (ChannelHandlerAdapter cha : WhttpServer.this.getChanneHandles()) {
               ch.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)cha });
             } 
           }
         });
     ChannelFuture future = b.bind(new InetSocketAddress(port)).sync();
     this.channelFutures.add(future);
     System.out.println("HTTP服务器启动，网址是 : http://localhost:" + port);
   }
   
   public void bind(List<ChannelHandlerAdapter> handlerAdapters, int port) throws InterruptedException {
     this.channeHandles.addAll(handlerAdapters);
     bind(port);
   }
   
   private List<ChannelFuture> channelFutures = new ArrayList<>();
   
   private EventLoopGroup bossGroup;
   
   private EventLoopGroup workerGroup;
   
   @PreDestroy
   public void shutdownGracefully() {
     try {
       for (ChannelFuture cf : this.channelFutures) {
         if (cf.channel() != null) {
           try {
             cf.channel().close();
           } catch (Exception e) {
             logger.error("通信server channel关闭异常", e);
           } 
         }
       } 
     } finally {
       this.bossGroup.shutdownGracefully();
       this.workerGroup.shutdownGracefully();
     } 
   }
   
   public List<ChannelHandlerAdapter> getChanneHandles() {
     return this.channeHandles;
   }
   
   public void setChanneHandles(List<ChannelHandlerAdapter> channeHandles) {
     this.channeHandles = channeHandles;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\server\WhttpServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */