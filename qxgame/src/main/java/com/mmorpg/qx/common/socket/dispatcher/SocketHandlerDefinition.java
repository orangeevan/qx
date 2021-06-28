package com.mmorpg.qx.common.socket.dispatcher;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.socket.core.Wsession;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 消息处理器描述
 * 
 * @author wangke
 * @since v1.0 2016年6月3日
 *
 */
public class SocketHandlerDefinition {
	private Object bean;
	private Method method;
	private Codec codec;

	public static SocketHandlerDefinition valueOf(Object bean, Method method, Codec codec) {
		SocketHandlerDefinition shd = new SocketHandlerDefinition();
		shd.bean = bean;
		shd.method = method;
		shd.codec = codec;
		return shd;
	}

	public Object invoke(Wsession session, Object packet) {
		return ReflectionUtils.invokeMethod(method, bean, session, packet);
	}

public Object invokeMQ(Object param){
		return ReflectionUtils.invokeMethod(method, bean, param);
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

	public Codec getCodec() {
		return codec;
	}

}
