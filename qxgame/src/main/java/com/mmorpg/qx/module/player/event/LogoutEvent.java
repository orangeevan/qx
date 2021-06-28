package com.mmorpg.qx.module.player.event;

import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.event.event.IEvent;

/**
 * 玩家登出事件
 * @author wang ke
 * @since v1.0 2019年2月7日
 *
 */
public class LogoutEvent implements IEvent {
	private Player player;

	public static LogoutEvent valueOf(Player player) {
		LogoutEvent loginEvent = new LogoutEvent();
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
