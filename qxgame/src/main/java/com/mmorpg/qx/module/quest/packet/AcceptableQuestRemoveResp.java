package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 
 * @author wang ke
 * @since v1.0 2018年4月3日
 *
 */
@SocketPacket(packetId = PacketId.ACCEPTABLEQUEST_REMOVE_RESP)
public class AcceptableQuestRemoveResp {
	@Protobuf
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
