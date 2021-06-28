package com.mmorpg.qx.module.player.event;

import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.event.event.IEvent;

/**
 * 玩家登陆事件
 * @author wang ke
 * @since v1.0 2019年2月7日
 *
 */
public class PlayerLevelUpEvent implements IEvent {
	private Player player;
	private int oldLevel;

	public static PlayerLevelUpEvent valueOf(Player player, int oldLevel) {
		PlayerLevelUpEvent levelUpEvent = new PlayerLevelUpEvent();
		levelUpEvent.player = player;
		levelUpEvent.oldLevel = oldLevel;
		return levelUpEvent;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public void setOldLevel(int oldLevel) {
		this.oldLevel = oldLevel;
	}

	@Override
	public long getOwner() {
		return player.getObjectId();
	}
}
