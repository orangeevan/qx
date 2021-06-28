package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 
 * 领奖成功返回
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
@SocketPacket(packetId = PacketId.REWARDQUEST_RESP)
public class RewardQuestResp {
	@Protobuf
	private int id;

	public static RewardQuestResp valueOf(int id) {
		RewardQuestResp cr = new RewardQuestResp();
		cr.id = id;
		return cr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
