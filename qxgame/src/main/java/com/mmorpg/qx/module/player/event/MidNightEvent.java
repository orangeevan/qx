package com.mmorpg.qx.module.player.event;

import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.event.event.IEvent;

/**
 * 跨天事件
 * @author wang ke
 * @since v1.0 2019年2月7日
 *
 */
public class MidNightEvent implements IEvent {
	private Player player;

	public static MidNightEvent valueOf(Player player) {
		MidNightEvent loginEvent = new MidNightEvent();
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
