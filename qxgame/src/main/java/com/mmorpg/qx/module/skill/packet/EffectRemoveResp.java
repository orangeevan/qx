package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.EFFECT_REMOVE_RESP)
public class EffectRemoveResp {
	@Protobuf(description = "")
	private long objectId;
	@Protobuf(description = "")
	private int effectId;

	public int getEffectId() {
		return effectId;
	}

	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

}
