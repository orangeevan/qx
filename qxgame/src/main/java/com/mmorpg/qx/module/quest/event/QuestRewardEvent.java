package com.mmorpg.qx.module.quest.event;

import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.event.event.IEvent;

public class QuestRewardEvent implements IEvent {

	private Player player;

	private int questId;

	public static QuestRewardEvent valueOf(Player player, int questId) {
		QuestRewardEvent resp = new QuestRewardEvent();
		resp.player = player;
		resp.questId = questId;
		return resp;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	@Override
	public long getOwner() {
		// TODO Auto-generated method stub
		return 0;
	}

}
