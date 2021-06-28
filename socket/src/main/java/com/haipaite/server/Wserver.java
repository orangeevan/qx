package com.haipaite.server;

import com.haipaite.coder.WpacketDecoder;
import com.haipaite.coder.WpacketEncoder;
import com.haipaite.config.ServerConfig;
import com.haipaite.dispatcher.SocketPacketHandler;
import com.haipaite.filter.firewall.DummyFirewallManager;
import com.haipaite.filter.firewall.FirewallManager;
import com.haipaite.filter.firewall.FlowFirewall;
import com.haipaite.filter.firewall.IpFirewall;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Wserver {
    private static final Logger logger = LoggerFactory.getLogger(Wserver.class);


    @Autowired
    private CustomHandlerManager customHandlerManager;


    private int[] ports;


    @Autowired
    private SessionHandler sessionHandler;


    @Autowired
    private FlowFirewall flowFirewall;

    @Autowired
    private IpFirewall ipFirewall;

    @Autowired
    private SocketPacketHandler socketPacketHandler;

    @Autowired
    private ServerConfig serverConfig;

    private int maxLength;


    public void open() {
        this.ipFirewall.open();
    }


    public void close() {
        this.ipFirewall.close();
    }

    @PostConstruct
    public void initProperties() throws IOException {
        String[] portstr = this.serverConfig.getProp("server.socket.address").split(",");
        this.ports = new int[portstr.length];
        int i = 0;
        for (String port : portstr) {
            this.ports[i] = Integer.valueOf(port).intValue();
            i++;
        }

        String maxLengthProp = this.serverConfig.getProp("server.socket.maxlength");
        if (maxLengthProp == null) {

            this.maxLength = 1048576;
        } else {
            this.maxLength = Integer.valueOf(maxLengthProp).intValue() * 1024;
        }
    }

    public void bind() throws InterruptedException, IOException {
        bind((FirewallManager) new DummyFirewallManager());
    }

    public void bind(FirewallManager firewallManager) throws InterruptedException, IOException {
        this.socketPacketHandler.setFirewallManager(firewallManager);
        NioEventLoopGroup nioEventLoopGroup1 = new NioEventLoopGroup();
        NioEventLoopGroup nioEventLoopGroup2 = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ((ServerBootstrap) ((ServerBootstrap) ((ServerBootstrap) serverBootstrap.group((EventLoopGroup) nioEventLoopGroup1, (EventLoopGroup) nioEventLoopGroup2).channel(NioServerSocketChannel.class))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT))
                .childOption(ChannelOption.SO_BACKLOG, Integer.valueOf(1024)).childOption(ChannelOption.SO_RCVBUF, Integer.valueOf(32768))
                .childOption(ChannelOption.SO_SNDBUF, Integer.valueOf(32768)).handler((ChannelHandler) new LoggingHandler(LogLevel.INFO)))
                .childHandler((ChannelHandler) new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast("session", (ChannelHandler) Wserver.this.sessionHandler);
                        sc.pipeline().addLast("flowFirewall", (ChannelHandler) Wserver.this.flowFirewall);

                        sc.pipeline().addLast("decoder", (ChannelHandler) new WpacketDecoder(Wserver.this.maxLength));
                        sc.pipeline().addLast("socketPacketHandler", (ChannelHandler) Wserver.this.socketPacketHandler);
                        sc.pipeline().addLast("encoder", (ChannelHandler) new WpacketEncoder());
                        for (ChannelHandlerAdapter cha : Wserver.this.customHandlerManager.getHandlers()) {
                            sc.pipeline().addLast(new ChannelHandler[]{(ChannelHandler) cha});
                        }
                    }
                });

        for (int port : this.ports) {
            ChannelFuture cf = serverBootstrap.bind(port);
            cf.sync();
            this.channelFutures.add(cf);
            System.err.println("服务器启动，绑定端口: " + port);
            logger.info("服务器启动，绑定端口: " + port);
        }
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
            System.err.println("通信层关闭");
            logger.info("通信层关闭");
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        }
    }

    public int[] getPorts() {
        return this.ports;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\server\Wserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */