package com.mmorpg.qx.common.redis;

public interface IRedisSerializer {

	public byte[] serialize(Object o);

	public <T> T deserialize(byte[] src, Class<T> cls);

}