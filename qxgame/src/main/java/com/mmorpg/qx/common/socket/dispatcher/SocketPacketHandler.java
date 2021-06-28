package com.mmorpg.qx.common.socket.dispatcher;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.common.collect.Maps;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.module.Module;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.common.socket.core.SessionManager;
import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.common.socket.core.WresponsePacket;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.common.socket.firewall.FirewallManager;
import com.mmorpg.qx.common.socket.utils.IpUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 消息处理器handler
 *
 * @author wangke
 * @since v1.0 2017年6月3日
 */
@Sharable
@Component
public class SocketPacketHandler extends ChannelInboundHandlerAdapter implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger logger = SysLoggerFactory.getLogger(SocketPacketHandler.class);

    /**
     * 接收packet与方法的映射
     */
    private ConcurrentHashMap<SocketHandleKey, SocketHandlerDefinition> handlerDefinitions = new ConcurrentHashMap<SocketHandleKey, SocketHandlerDefinition>();

    /**
     * 类class与packetId的快速映射
     */
    private Map<Class<?>, SocketHandleKey> socketClassKeys = new ConcurrentHashMap<Class<?>, SocketHandleKey>();


    /**
     * 消息只有协议ID，没有消息内容，混存对象
     */
    private Map<Class<?>, Object> singlePacketMsg = new ConcurrentHashMap<>();
    @Autowired
    private SessionManager sessionManager;

    private FirewallManager firewallManager;

    private static SocketPacketHandler self;

    private ApplicationContext applicationContext;
    private Map<Short, Codec> packetIdToCodec = Maps.newConcurrentMap();
    private Map<Short, Class<?>> packetIdToClass = Maps.newConcurrentMap();
    private Map<Class<?>, Short> classToPacketId = Maps.newConcurrentMap();

    @PostConstruct
    private void init() {
        self = this;
        String[] className = applicationContext.getBeanNamesForAnnotation(SocketPacket.class);
        if (ArrayUtils.isNotEmpty(className)) {
            for (String name : className) {
                Class<?> packetClass = applicationContext.getType(name);
                SocketPacket annotation = packetClass.getAnnotation(SocketPacket.class);
                short packetId = (short) (annotation.module() > 0 ? annotation.module() * Module.MAX + annotation.packetId() : annotation.packetId());
                Codec<?> create = null;

                try {
                    create = ProtobufProxy.create(packetClass);
                    if (packetIdToCodec.put(packetId, create) != null) {
                        throw new RuntimeException("消息ID重复使用 ID:" + annotation.packetId());
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                }

                packetIdToClass.put(packetId, packetClass);
                classToPacketId.put(packetClass, packetId);
                try {
                    Object o = packetClass.newInstance();
                    singlePacketMsg.put(packetClass, o);
                } catch (Exception e) {
                    logger.error("缓存空协异常 ", e);
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(SocketClass.class)) {
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(SocketMethod.class)) {
                    continue;
                }

                // 参数和返回值验证
                Class<?>[] clzs = method.getParameterTypes();
                if (clzs.length != 2) {
                    throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "只能拥有两个参数。");
                }
                if (!Wsession.class.isAssignableFrom(clzs[0])) {
                    throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "第一个参数必须为[WSession]类型。");
                }
                if (clzs[1].getAnnotation(SocketPacket.class) == null) {
                    throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "第二个参数必须为包含SocketClass注解。");
                }
                if (method.getReturnType().getName() != "void") {
                    if (method.getReturnType().getAnnotation(SocketPacket.class) == null) {
                        throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "返回值必须包含SocketClass注解。");
                    }
                }

                // 接收参数验证
                Class<?> parameterPacketClass = method.getParameterTypes()[1];
                Class<?> returnPacketClass = method.getReturnType();
                for (Class<?> packetClass : new Class<?>[]{parameterPacketClass, returnPacketClass}) {
                    // 接收参数
                    if ("void".equals(packetClass.getName())) {
                        // 返回参数为void
                        continue;
                    }
                    SocketHandleKey key = socketClassKeys.get(packetClass);
                    if (key != null) {
                        // 该通信包已经注册过
                        throw new IllegalArgumentException(String.format("接收参数[%s]已经注册过,但又尝试在[%s]中注册!", packetClass, bean.getClass()));
                    }
                    SocketPacket socketPacket = packetClass.getAnnotation(SocketPacket.class);
                    if (socketPacket == null) {
                        // 该对象没有class注册
                        throw new IllegalArgumentException(String.format("class[%s]没有包含SocketPacket注解!", packetClass));
                    }
                    short packetId = (short) (socketPacket.module() > 0 ? socketPacket.module() * Module.MAX + socketPacket.packetId() : socketPacket.packetId());
                    key = SocketHandleKey.valueOf(packetId);
                    socketClassKeys.put(packetClass, key);
                    // 是否有其它执行对象已经注册了此消息
                    SocketHandlerDefinition shd = handlerDefinitions.get(key);
                    if (shd != null) {
                        throw new IllegalArgumentException(String.format("class[%s]和class[%s],packetId[%s]重复使用,一个packetId只能用在一个方法上!", shd.getBean().getClass(), packetClass, socketPacket.packetId()));
                    }
                    try {
                        Codec codec = null;
                        try {
                            codec = ProtobufProxy.create(packetClass, false, null);
                        } catch (Exception e) {

                        }
                        handlerDefinitions.put(key, SocketHandlerDefinition.valueOf(bean, method, codec));
                    } catch (Throwable e) {
                        e.printStackTrace();
                        logger.error(String.format("初始化[%s]生成SocketHandlerDefinition失败!", packetClass), e);
                        throw new IllegalArgumentException("初始化生成SocketHandlerDefinition失败!");
                    }
                }
            }
        }

        return bean;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final WrequestPacket packet = (WrequestPacket) msg;
        SocketHandleKey key = SocketHandleKey.valueOf(packet.getPacketId());
        final SocketHandlerDefinition shd = handlerDefinitions.get(key);
        if (shd == null) {
            logger.error(String.format("没有找到处理[%s]的SocketHandlerDefinition。", key));
            return;
        }
        logger.info("收到客户端【{}】 消息【{}】", ctx.channel().remoteAddress(), packet.getPacketId());
        final Channel channel = ctx.channel();
        //有些消息是没有消息体，直接生成空消息，没有codec
        Object mo = null;
        if (shd.getCodec() == null) {
            mo = singlePacketMsg.get(packetIdToClass.get(packet.getPacketId()));
        } else {
            try {
                mo = shd.getCodec().decode(packet.getData());
            } catch (Exception e) {
                logger.error("解析消息: " + packet.getPacketId() + " 异常", e);
                return;
            }
        }
        final Object message = mo;
        final Wsession session = sessionManager.getSession(ctx.channel().id());
        if (!firewallManager.packetFilter(session, packet)) {
            logger.warn(String.format("session[%s] packetId[%s]发送非法的消息!", IpUtils.getIp(ctx.channel().localAddress()), packet.getPacketId()));
            //非法的消息包直接丢掉
            return;
        }
        IdentityEventExecutorGroup.addTask(new AbstractDispatcherHashCodeRunable() {

            @Override
            public void doRun() {
                try {
                    Object returnMessage = shd.invoke(session, message);
                    if (returnMessage != null) {
                        SocketHandleKey socketHandleKey = socketClassKeys.get(returnMessage.getClass());
                        if (socketHandleKey != null) {
                            Codec codec = handlerDefinitions.get(socketHandleKey).getCodec();
                            byte[] returnMessageBytes = new byte[0];
                            //部分协议没有消息体，只有消息id，没有codec
                            if (codec != null) {
                                returnMessageBytes = codec.encode(returnMessage);
                            }
                            WresponsePacket wp = WresponsePacket.valueOf(socketHandleKey.getPacketId(), returnMessageBytes);
                            channel.writeAndFlush(wp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("SocketHandlerDefinition任务执行失败!", e);
                }

            }

            @Override
            public long timeoutNanoTime() {
                // 3毫秒
                return 3 * 1000 * 1000;
            }

            @Override
            public String name() {
                return "socket_" + packet.getPacketId();
            }

            @Override
            public int getDispatcherHashCode() {
                return session.selectDispatcherHashCode();
            }
        });
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("通信包异常!", cause);
        ctx.close();
    }

    public FirewallManager getFirewallManager() {
        return firewallManager;
    }

    public void setFirewallManager(FirewallManager firewallManager) {
        this.firewallManager = firewallManager;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 发送消息
     */
    public ChannelFuture sendMsg(Wsession session, Channel channel, Object msg, boolean flush) {
        try {
            short packetId = classToPacketId.get(msg.getClass());
            byte[] messageByte = new byte[0];
            Codec codec = packetIdToCodec.get(packetId);
            //有些消息只有头没有数据
            if (codec != null) {
                messageByte = codec.encode(msg);
            }
            WresponsePacket packet = WresponsePacket.valueOf(packetId, messageByte);
            if (flush) {
                return channel.writeAndFlush(packet);
            }
            ChannelFuture future = channel.write(packet);
            if (session.getFlushTimer().compareAndSet(false, true)) {
                delayTimer.newTimeout(timeout -> {
                    session.getFlushTimer().compareAndSet(true, false);
                    channel.flush();

                }, 100, TimeUnit.MILLISECONDS);
            }
            return future;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashedWheelTimer delayTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);

    public static SocketPacketHandler getInstance() {
        return self;
    }
}
