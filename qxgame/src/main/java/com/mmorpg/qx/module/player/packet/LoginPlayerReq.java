package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 登陆请求
 * @author wang ke
 * @since v1.0 2019年2月8日
 *
 */
@SocketPacket(packetId = PacketId.LOGIN_PLAYER_REQ)
public class LoginPlayerReq {
	@Protobuf
	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

}
