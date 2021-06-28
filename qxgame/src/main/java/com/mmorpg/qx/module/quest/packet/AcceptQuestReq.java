package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 客户端接取任务
 * @author wang ke
 * @since v1.0 2018年3月28日
 *
 */
@SocketPacket(packetId = PacketId.ACCEPTQUEST_REQ)
public class AcceptQuestReq {
	/** 任务id */
	@Protobuf
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
