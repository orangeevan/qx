package com.mmorpg.qx.module.player.packet;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.player.packet.vo.PlayerShortInfo;

/**
 * 返回所有该账号所有的角色
 * 
 * @author wang ke
 * @since v1.0 2019年2月8日
 *
 */
@SocketPacket(packetId = PacketId.GET_PLAYER_LIST_RESP)
public class GetPlayerListResp {

	@Protobuf(description = "角色信息列表")
	private List<PlayerShortInfo> playerItems = new ArrayList<>();

	public List<PlayerShortInfo> getPlayerItems() {
		return playerItems;
	}

	public void setPlayerItems(List<PlayerShortInfo> playerItems) {
		this.playerItems = playerItems;
	}

}
