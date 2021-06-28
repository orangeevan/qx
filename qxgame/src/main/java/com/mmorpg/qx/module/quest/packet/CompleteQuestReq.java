package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 客户端完成任务,操作类型的
 * @author wang ke
 * @since v1.0 2018年3月28日
 *
 */
@SocketPacket(packetId = PacketId.COMPLETEQUEST_REQ)
public class CompleteQuestReq {
	@Protobuf
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
