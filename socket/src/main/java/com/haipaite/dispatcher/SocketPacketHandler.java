package com.haipaite.dispatcher;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.common.collect.Maps;
import com.haipaite.annotation.SocketClass;
import com.haipaite.annotation.SocketMethod;
import com.haipaite.annotation.SocketPacket;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.haipaite.common.utility.FileUtils;
import com.haipaite.config.ServerConfig;
import com.haipaite.core.SessionManager;
import com.haipaite.core.WrequestPacket;
import com.haipaite.core.WresponsePacket;
import com.haipaite.core.Wsession;
import com.haipaite.filter.firewall.FirewallManager;
import com.haipaite.utils.IpUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Sharable
@Component
public class SocketPacketHandler
        extends ChannelInboundHandlerAdapter
        implements BeanPostProcessor, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SocketPacketHandler.class);


    private ConcurrentHashMap<SocketHandleKey, SocketHandlerDefinition> handlerDefinitions = new ConcurrentHashMap<>();


    private Map<Class<?>, SocketHandleKey> socketClassKeys = new ConcurrentHashMap<>();


    private Map<Class<?>, Object> singlePacketMsg = new ConcurrentHashMap<>();

    @Autowired
    private SessionManager sessionManager;

    private FirewallManager firewallManager;

    private static SocketPacketHandler self;

    private ApplicationContext applicationContext;
    private Map<Short, Codec> packetIdToCodec = Maps.newConcurrentMap();
    private Map<Short, Class<?>> packetIdToClass = Maps.newConcurrentMap();
    private Map<Class<?>, Short> classToPacketId = Maps.newConcurrentMap();
    @Autowired
    ServerConfig serverConfig;

    @PostConstruct
    private void init() {
        self = this;
        String[] className = this.applicationContext.getBeanNamesForAnnotation(SocketPacket.class);
        if (ArrayUtils.isNotEmpty((Object[]) className)) {
            for (String name : className) {
                Class<?> packetClass = this.applicationContext.getType(name);
                SocketPacket annotation = packetClass.<SocketPacket>getAnnotation(SocketPacket.class);
                Codec<?> create = null;
                try {
                    create = ProtobufProxy.create(packetClass);
                    this.packetIdToCodec.put(Short.valueOf(annotation.packetId()), create);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                }
                this.packetIdToClass.put(Short.valueOf(annotation.packetId()), packetClass);
                this.classToPacketId.put(packetClass, Short.valueOf(annotation.packetId()));
                try {
                    Object o = packetClass.newInstance();
                    this.singlePacketMsg.put(packetClass, o);
                    iDLGenerator(packetClass, annotation.packetId());
                } catch (Exception e) {
                    logger.error("缓存空协异常 ", e);
                    System.err.println(e);
                }
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent((Class) SocketClass.class)) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent((Class) SocketMethod.class)) {


                    Class<?>[] clzs = method.getParameterTypes();
                    if (clzs.length != 2) {
                        throw new IllegalArgumentException(bean
                                .getClass().getName() + "." + method.getName() + "只能拥有两个参数。");
                    }
                    if (!Wsession.class.isAssignableFrom(clzs[0])) {
                        throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "第一个参数必须为[com.windforce.core.WSession]类型。");
                    }

                    if (clzs[1].getAnnotation(SocketPacket.class) == null) {
                        throw new IllegalArgumentException(bean
                                .getClass().getName() + "." + method.getName() + "第二个参数必须为包含SocketClass注解。");
                    }
                    if (method.getReturnType().getName() != "void" &&
                            method.getReturnType().getAnnotation(SocketPacket.class) == null) {
                        throw new IllegalArgumentException(bean
                                .getClass().getName() + "." + method.getName() + "返回值必须包含SocketClass注解。");
                    }


                    Class<?> parameterPacketClass = method.getParameterTypes()[1];
                    Class<?> returnPacketClass = method.getReturnType();
                    for (Class<?> packetClass : new Class[]{parameterPacketClass, returnPacketClass}) {

                        if (!"void".equals(packetClass.getName())) {


                            SocketHandleKey key = this.socketClassKeys.get(packetClass);
                            if (key != null) {
                                throw new IllegalArgumentException(
                                        String.format("接收参数[%s]已经注册过,但又尝试在[%s]中注册!", new Object[]{packetClass, bean.getClass()}));
                            }
                            SocketPacket socketPacket = packetClass.<SocketPacket>getAnnotation(SocketPacket.class);
                            if (socketPacket == null) {
                                throw new IllegalArgumentException(String.format("class[%s]没有包含SocketPacket注解!", new Object[]{packetClass}));
                            }
                            key = SocketHandleKey.valueOf(socketPacket.packetId());
                            this.socketClassKeys.put(packetClass, key);

                            SocketHandlerDefinition shd = this.handlerDefinitions.get(key);
                            if (shd != null) {
                                throw new IllegalArgumentException(
                                        String.format("class[%s]和class[%s],packetId[%s]重复使用,一个packetId只能用在一个方法上!", new Object[]{
                                                shd.getBean().getClass(), packetClass, Short.valueOf(socketPacket.packetId())
                                        }));
                            }
                            try {
                                Codec codec = null;
                                try {
                                    codec = ProtobufProxy.create(packetClass, false, null);
                                } catch (Exception exception) {
                                }


                                this.handlerDefinitions.put(key, SocketHandlerDefinition.valueOf(bean, method, codec));
                                iDLGenerator(packetClass, key.getPacketId());
                            } catch (Throwable e) {
                                e.printStackTrace();
                                logger.error(String.format("初始化[%s]生成SocketHandlerDefinition失败!", new Object[]{packetClass}), e);
                                throw new IllegalArgumentException("初始化生成SocketHandlerDefinition失败!");
                            }
                        }
                    }
                }
            }
        }
        return bean;
    }

    private void iDLGenerator(Class<?> clazz, short packetId) throws Exception {
        String protoDir = this.serverConfig.getProp("server.proto.dir");
        if (StringUtils.isBlank(protoDir)) {
            return;
        }
        String idl = ProtobufIDLGenerator.getIDL(clazz, null, null, true);
        File file = new File(protoDir + packetId + "_" + clazz.getSimpleName() + ".proto");
        FileUtils.createFile(file);
        FileWriter fw = new FileWriter(file);
        fw.write(idl);
        fw.flush();
        fw.close();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final WrequestPacket packet = (WrequestPacket) msg;
        SocketHandleKey key = SocketHandleKey.valueOf(packet.getPacketId());
        final SocketHandlerDefinition shd = this.handlerDefinitions.get(key);
        if (shd == null) {
            logger.error(String.format("没有找到处理[%s]的SocketHandlerDefinition。", new Object[]{key}));
            return;
        }
        if (packet.getPacketId() == 37) {
            System.err.println((packet.getData()).length);
        }
        final Channel channel = ctx.channel();

        Object mo = null;
        if (shd.getCodec() == null) {
            mo = this.singlePacketMsg.get(this.packetIdToClass.get(Short.valueOf(packet.getPacketId())));
        } else {
            mo = shd.getCodec().decode(packet.getData());
        }
        final Object message = mo;
        final Wsession session = this.sessionManager.getSession(ctx.channel().id());
        if (!this.firewallManager.packetFilter(session, packet)) {
            logger.warn(String.format("session[%s] packetId[%s]发送非法的消息!", new Object[]{IpUtils.getIp(ctx.channel().localAddress()), Short.valueOf(packet.getPacketId())}));

            return;
        }
        IdentityEventExecutorGroup.addTask(new AbstractDispatcherHashCodeRunable() {
            @Override
            public void doRun() {
                try {
                    Object returnMessage = shd.invoke(session, message);
                    if (returnMessage != null) {
                        SocketHandleKey socketHandleKey = (SocketHandleKey) SocketPacketHandler.this.socketClassKeys.get(returnMessage.getClass());
                        if (socketHandleKey != null) {
                            Codec codec = ((SocketHandlerDefinition) SocketPacketHandler.this.handlerDefinitions.get(socketHandleKey)).getCodec();
                            byte[] returnMessageBytes = new byte[0];

                            if (codec != null) {
                                returnMessageBytes = codec.encode(returnMessage);
                            }
                            WresponsePacket wp = WresponsePacket.valueOf(socketHandleKey.getPacketId(), returnMessageBytes);
                            channel.writeAndFlush(wp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SocketPacketHandler.logger.error("SocketHandlerDefinition任务执行失败!", e);
                }
            }

            @Override
            public long timeoutNanoTime() {
                return 3000000L;
            }

            @Override
            public String name() {
                return "wsocket_" + packet.getPacketId();
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
        return this.firewallManager;
    }

    public void setFirewallManager(FirewallManager firewallManager) {
        this.firewallManager = firewallManager;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public ChannelFuture sendMsg(final Wsession session, final Channel channel, Object msg, boolean flush) {
        try {
            short packetId = ((Short) this.classToPacketId.get(msg.getClass())).shortValue();
            byte[] messageByte = new byte[0];
            Codec codec = this.packetIdToCodec.get(Short.valueOf(packetId));

            if (codec != null) {
                messageByte = codec.encode(msg);
            }
            WresponsePacket packet = WresponsePacket.valueOf(packetId, messageByte);
            if (flush) {
                return channel.writeAndFlush(msg);
            }
            ChannelFuture future = channel.write(packet);
            if (session.getFlushTimer().compareAndSet(false, true)) {
                this.delayTimer.newTimeout(new TimerTask() {
                    @Override
                    public void run(Timeout timeout) throws Exception {
                        session.getFlushTimer().compareAndSet(true, false);
                        channel.flush();
                    }
                }, 100L, TimeUnit.MILLISECONDS);
            }

            return future;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private HashedWheelTimer delayTimer = new HashedWheelTimer(100L, TimeUnit.MILLISECONDS);

    public static SocketPacketHandler getInstance() {
        return self;
    }

    public void generateIDL() {
        this.classToPacketId.forEach((key, value) -> {
            try {
                iDLGenerator(key, value.shortValue());
            } catch (Exception e) {
                System.err.println(e);
            }
        });
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\dispatcher\SocketPacketHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */