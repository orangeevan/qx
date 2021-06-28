package com.mmorpg.qx.common.socket.server;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 服务器
 *
 * @author wangke
 * @since v1.0 2016年6月3日
 */
@Component
public class WhttpServer {
    private static final Logger logger = SysLoggerFactory.getLogger(WhttpServer.class);

    private List<ChannelHandlerAdapter> channeHandles = new ArrayList<>();

    public void bind(final int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //添加HTTPS请求处理
                       /* SelfSignedCertificate ssc = new SelfSignedCertificate();
                        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                        SSLEngine sslEngine = sslCtx.newEngine(UnpooledByteBufAllocator.DEFAULT);
                        ch.pipeline().addFirst( new SslHandler(sslEngine));*/

                        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
                        ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                        for (ChannelHandlerAdapter cha : getChanneHandles()) {
                            ch.pipeline().addLast(cha);
                        }
                    }
                });
        ChannelFuture future = b.bind(new InetSocketAddress(port)).sync();
        channelFutures.add(future);
        System.out.println("HTTP服务器启动，网址是 : " + "http://localhost:" + port);
    }

    public void bind(List<ChannelHandlerAdapter> handlerAdapters, final int port) throws InterruptedException {
        this.channeHandles.addAll(handlerAdapters);
        this.bind(port);
    }

    private List<ChannelFuture> channelFutures = new ArrayList<>();

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @PreDestroy
    public void shutdownGracefully() {
        try {
            for (ChannelFuture cf : channelFutures) {
                if (cf.channel() != null) {
                    try {
                        cf.channel().close();
                    } catch (Exception e) {
                        logger.error("通信server channel关闭异常", e);
                    }
                }
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public List<ChannelHandlerAdapter> getChanneHandles() {
        return channeHandles;
    }

    public void setChanneHandles(List<ChannelHandlerAdapter> channeHandles) {
        this.channeHandles = channeHandles;
    }
}
