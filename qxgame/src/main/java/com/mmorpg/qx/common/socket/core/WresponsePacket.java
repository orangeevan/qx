package com.mmorpg.qx.common.socket.core;

/**
 * 
 * @author wangke
 * @since v1.0 2018年2月6日
 *
 */
public class WresponsePacket {
	private short packetId;
	private byte[] data;

	public static WresponsePacket valueOf(short packetId, byte[] data) {
		WresponsePacket wp = new WresponsePacket();
		wp.setPacketId(packetId);
		wp.data = data;
		return wp;
	}

	public short getPacketId() {
		return packetId;
	}

	public void setPacketId(short packetId) {
		this.packetId = packetId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
