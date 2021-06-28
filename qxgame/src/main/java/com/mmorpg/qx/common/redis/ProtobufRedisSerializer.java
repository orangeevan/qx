package com.mmorpg.qx.common.redis;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

import java.io.IOException;

public class ProtobufRedisSerializer implements IRedisSerializer {

	@Override
	public byte[] serialize(Object o) {
		Codec codec = ProtobufProxy.create(o.getClass());
		try {
			return codec.encode(o);
		} catch (IOException e) {
			throw new IllegalArgumentException("serialize error", e);
		}
	}

	@Override
	public <T> T deserialize(byte[] src, Class<T> cls) {
		Codec<T> codec = ProtobufProxy.create(cls);
		try {
			return codec.decode(src);
		} catch (IOException e) {
			throw new IllegalArgumentException("deserialize error", e);
		}
	}

}
