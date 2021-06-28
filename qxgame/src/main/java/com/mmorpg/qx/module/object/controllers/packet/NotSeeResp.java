package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.NOT_SEE_RESP)
public class NotSeeResp {

	@Protobuf
	private long objectId;

	public static NotSeeResp valueOf(long objectId) {
		NotSeeResp resp = new NotSeeResp();
		resp.objectId = objectId;
		return resp;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
}
