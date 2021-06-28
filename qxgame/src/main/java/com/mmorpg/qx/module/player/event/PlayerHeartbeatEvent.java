package com.mmorpg.qx.module.player.event;

import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.event.event.IEvent;

/**
 * 玩家系统心跳事件
 * @author wang ke
 * @since v1.0 2019年2月7日
 *
 */
public class PlayerHeartbeatEvent implements IEvent {

	private Player player;

	public static PlayerHeartbeatEvent valueOf(Player player) {
		PlayerHeartbeatEvent loginEvent = new PlayerHeartbeatEvent();
		loginEvent.player = player;
		return loginEvent;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public long getOwner() {
		return player.getObjectId();
	}
}
