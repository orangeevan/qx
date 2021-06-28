package com.mmorpg.qx.module.moduleopen.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.moduleopen.service.ModuleOpenService;
import com.mmorpg.qx.module.player.event.LoginEvent;
import com.mmorpg.qx.module.player.event.PlayerLevelUpEvent;
import com.mmorpg.qx.module.quest.event.QuestRewardEvent;
import com.mmorpg.qx.module.quest.resource.QuestResource;
import com.mmorpg.qx.module.quest.service.QuestManager;
import com.haipaite.common.event.anno.ReceiverAnno;

@Component
public class ModuleOpenFacade {

	@Autowired
	private ModuleOpenService moduleOpenService;

	@ReceiverAnno
	public void playerLoginEvent(LoginEvent loginEvent) {
		moduleOpenService.refresh(loginEvent.getPlayer());
	}

	@ReceiverAnno
	public void level(PlayerLevelUpEvent levelUpEvent) {
		moduleOpenService.refresh(levelUpEvent.getPlayer());
	}

	@ReceiverAnno
	public void questReward(QuestRewardEvent questRewardEvent) {
		QuestResource questResource = QuestManager.getInstance().getQuestResource(questRewardEvent.getQuestId());
		if (questResource.getModuleId() != 0) {
			moduleOpenService.refresh(questRewardEvent.getPlayer());
		}
	}

}
