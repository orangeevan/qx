package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 前端可接取任务
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
@SocketPacket(packetId = PacketId.ACCEPTABLEQUEST_ADD_RESP)
public class AcceptableQuestAddResp {
	@Protobuf
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
