package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

//@SocketPacket(packetId = PacketId.ENTER_WORLD_REQ)
public class EnterWorldReq {
	@Protobuf(description = "进入世界的编号")
	private int sign;

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

}
