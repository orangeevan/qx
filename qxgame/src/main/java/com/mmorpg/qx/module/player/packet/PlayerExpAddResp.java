package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 登陆请求回复包含玩家的大量信息
 * @author wang ke
 * @since v1.0 2019年2月8日
 *
 */
@SocketPacket(packetId = PacketId.PLAYER_EXPADD_RESP)
public class PlayerExpAddResp {
	@Protobuf(description = "增加的经验,有可能为负值")
	private long addExp;
	@Protobuf(description = "当前的经验")
	private long currentExp;
	@Protobuf(description = "当前的等级")
	private int level;

	public static PlayerExpAddResp valueOf(long addExp, long currentExp, int level) {
		PlayerExpAddResp resp = new PlayerExpAddResp();
		resp.addExp = addExp;
		resp.currentExp = currentExp;
		resp.level = level;
		return resp;
	}

	public long getCurrentExp() {
		return currentExp;
	}

	public void setCurrentExp(long currentExp) {
		this.currentExp = currentExp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
