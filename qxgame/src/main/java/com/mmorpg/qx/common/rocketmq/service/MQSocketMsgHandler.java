package com.mmorpg.qx.common.rocketmq.service;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.rocketmq.anno.MQSocketClass;
import com.mmorpg.qx.common.rocketmq.anno.MQSocketMethod;
import com.mmorpg.qx.common.rocketmq.model.IMsgBody;
import com.mmorpg.qx.common.socket.dispatcher.SocketHandlerDefinition;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wang ke
 * @description:
 * @since 16:12 2021/6/10
 */
@Component
public class MQSocketMsgHandler implements BeanPostProcessor, IMQMsgHandler {

    private Map<String, SocketHandlerDefinition> definitions = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(MQSocketClass.class)) {
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MQSocketMethod.class)) {
                    continue;
                }

                // 参数和返回值验证
                Class<?>[] clzs = method.getParameterTypes();
                if (clzs.length != 1) {
                    throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "只能拥有两个参数。");
                }
                if (!IMsgBody.class.isAssignableFrom(clzs[0])) {
                    throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + "第一个参数必须为[IMsgBody]类型。");
                }
                // 接收参数验证
                Class<?> parameterPacketClass = method.getParameterTypes()[0];
                if (definitions.containsKey(parameterPacketClass)) {
                    // 该MQ通信包已经注册过
                    throw new IllegalArgumentException(String.format("MQ接收参数[%s]已经注册过,但又尝试在[%s]中注册!", parameterPacketClass, bean.getClass()));
                }
                try {
                    Codec codec = ProtobufProxy.create(parameterPacketClass, false, null);
                    definitions.put(parameterPacketClass.getSimpleName(), SocketHandlerDefinition.valueOf(bean, method, codec));
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("初始化生成MQSocketMsgHandler definition失败!");
                }
            }
        }
        return bean;
    }

    /**
     * 解析协议，执行逻辑
     *
     * @param messageExt
     */
    @Override
    public void handleMsg(MessageExt messageExt) {
        try {
            String clzName = messageExt.getTags();
            if (!definitions.containsKey(clzName)) {
                return;
            }
            SocketHandlerDefinition handlerDefinition = definitions.get(clzName);
            Object decode = handlerDefinition.getCodec().decode(messageExt.getBody());
            handlerDefinition.invokeMQ(decode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encode(IMsgBody body) throws IOException {
        SocketHandlerDefinition handlerDefinition = definitions.get(body.getClass().getSimpleName());
        if (Objects.nonNull(handlerDefinition)) {
            return handlerDefinition.getCodec().encode(body);
        }
        throw new IllegalArgumentException("不支持的对象编码 " + body);
    }

    @Override
    public <M extends IMsgBody> M decode(Class mClass, byte[] bytes) throws IOException {
        SocketHandlerDefinition handlerDefinition = definitions.get(mClass.getSimpleName());
        if (Objects.nonNull(handlerDefinition)) {
            return (M) handlerDefinition.getCodec().decode(bytes);
        }
        throw new IllegalArgumentException("不支持的对象编码 " + mClass);
    }

    public static MQSocketMsgHandler getInstance() {
        return BeanService.getBean(MQSocketMsgHandler.class);
    }
}
