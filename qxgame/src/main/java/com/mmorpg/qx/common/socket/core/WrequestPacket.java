package com.mmorpg.qx.common.socket.core;

/**
 * 基础包
 * 
 * @author wangke
 * @since v1.0 2018年1月31日
 *
 */
public class WrequestPacket {

	private short packetId;
	private byte[] data;

	public static WrequestPacket valueOf(short packetId, byte[] data) {
		WrequestPacket wp = new WrequestPacket();
		wp.setPacketId(packetId);
		wp.data = data;
		return wp;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public short getPacketId() {
		return packetId;
	}

	public void setPacketId(short packetId) {
		this.packetId = packetId;
	}


	@Override
	public String toString() {
		return "PacketId: "+packetId+"  data: "+data;
	}
}
