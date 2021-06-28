package com.mmorpg.qx.common.socket.dispatcher;

/**
 * 通信号
 * 
 * @author wangke
 * @since v1.0 2016年6月3日
 *
 */
public class SocketHandleKey {
	private short packetId;

	public static SocketHandleKey valueOf(short packetId) {
		SocketHandleKey key = new SocketHandleKey();
		key.packetId = packetId;
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + packetId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SocketHandleKey other = (SocketHandleKey) obj;
		if (packetId != other.packetId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "{packetId:" + packetId + "}";
	}

	public short getPacketId() {
		return packetId;
	}

}
