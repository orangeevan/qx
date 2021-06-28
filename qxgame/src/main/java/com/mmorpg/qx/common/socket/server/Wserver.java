package com.mmorpg.qx.common.socket.server;


import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.coder.WpacketDecoder;
import com.mmorpg.qx.common.socket.coder.WpacketEncoder;
import com.mmorpg.qx.common.socket.config.ServerConfigConstant;
import com.mmorpg.qx.common.socket.config.ServerSocketConfig;
import com.mmorpg.qx.common.socket.dispatcher.SocketPacketHandler;
import com.mmorpg.qx.common.socket.firewall.DummyFirewallManager;
import com.mmorpg.qx.common.socket.firewall.FirewallManager;
import com.mmorpg.qx.common.socket.firewall.FlowFirewall;
import com.mmorpg.qx.common.socket.firewall.IpFirewall;
import com.mmorpg.qx.module.rank.manager.RankManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Socket服务器
 *
 * @author wangke
 * @since v1.0 2016年6月3日
 */
@Component
public class Wserver {

    private static final Logger logger = SysLoggerFactory.getLogger(Wserver.class);

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
    private ServerSocketConfig serverConfig;

    private int maxLength;

    /**
     * 开启
     *
     * @return
     */
    public void open() {
        ipFirewall.open();
    }

    /**
     * 关闭
     *
     * @return
     */
    public void close() {
        ipFirewall.close();
    }

    @PostConstruct
    public void initProperties() throws IOException {
        String[] portstr = serverConfig.getProp(ServerConfigConstant.KEY_ADDRESS).split(",");
        ports = new int[portstr.length];
        int i = 0;
        for (String port : portstr) {
            ports[i] = Integer.valueOf(port);
            i++;
        }

        String maxLengthProp = serverConfig.getProp(ServerConfigConstant.PACKET_MAXLENGTH);
        if (maxLengthProp == null) {
            // 默认1m
            maxLength = 1024 * 1024;
        } else {
            maxLength = Integer.valueOf(maxLengthProp) * 1024;
        }
    }

    public void bind() throws InterruptedException, IOException {
        bind(new DummyFirewallManager());
    }

    public void bind(FirewallManager firewallManager) throws InterruptedException, IOException {
        socketPacketHandler.setFirewallManager(firewallManager);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 1024 * 128)
                .childOption(ChannelOption.SO_SNDBUF, 1024 * 128).handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast("session", sessionHandler);
                        sc.pipeline().addLast("flowFirewall", flowFirewall);

                        //sc.pipeline().addLast("ipFirewall", ipFirewall);
                        sc.pipeline().addLast("decoder", new WpacketDecoder(maxLength));
                        sc.pipeline().addLast("socketPacketHandler", socketPacketHandler);
                        sc.pipeline().addLast("encoder", new WpacketEncoder());
                        for (ChannelHandlerAdapter cha : customHandlerManager.getHandlers()) {
                            sc.pipeline().addLast(cha);
                        }
                    }
                });

        for (int port : ports) {
            ChannelFuture cf = serverBootstrap.bind(port);
            cf.sync();
            channelFutures.add(cf);
            logger.info("服务器启动，绑定端口: " + port);
        }
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
            try {
                logger.info("通信层关闭");
                System.err.println("通信层关闭");
                if (Objects.nonNull(bossGroup)) {
                    bossGroup.shutdownGracefully();
                }
                if (Objects.nonNull(workerGroup)) {
                    workerGroup.shutdownGracefully();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        //关闭事件服务
        try {
            logger.info("事件服务层关闭");
            EventBusManager.getInstance().shutdown();
        } catch (Exception e) {
            logger.error("事件线程关闭异常", e);
        }

        //关闭排行榜
        try {
            logger.info("排行榜服务关闭");
            RankManager.getInstance().shutdown();
        } catch (Exception e) {
            logger.error("排行榜服务关闭异常", e);
        }

        //关闭业务层
        try {
            IdentityEventExecutorGroup.shutdown();
            System.err.println("IdentityEventExecutorGroup 业务线程已经全部关闭");
            logger.info("IdentityEventExecutorGroup 业务线程已经全部关闭");
        } catch (Exception e) {
            logger.error("业务线程关闭异常", e);
        }
    }

    public int[] getPorts() {
        return ports;
    }
}
