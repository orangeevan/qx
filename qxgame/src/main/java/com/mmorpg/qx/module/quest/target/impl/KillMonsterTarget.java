package com.mmorpg.qx.module.quest.target.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.object.controllers.event.KillMonsterEvent;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.model.Quest;
import com.mmorpg.qx.module.quest.target.QuestTarget;
import com.mmorpg.qx.module.quest.target.QuestTargetDef;
import com.mmorpg.qx.module.quest.target.QuestTargetHandler;
import com.mmorpg.qx.module.quest.target.QuestTargetType;
import com.haipaite.common.event.anno.ReceiverAnno;

/**
 * 普通杀怪任务目标
 * @author wang ke
 * @since v1.0 2018年4月3日
 *
 */
@Component
public class KillMonsterTarget extends QuestTargetHandler {

	private static final String MONSTERID = "monsterId";

	@Override
	public QuestTargetType getType() {
		return QuestTargetType.MONSTER_HUNT;
	}

	@Override
	public QuestTarget create(QuestTargetDef targetDef, Player player) {
		QuestTarget target = new QuestTarget();
		target.setQuestTargetType(getType());
		if (targetDef.getParms() != null) {
			target.setParms(new HashMap<>(1));
			target.getParms().put(MONSTERID, targetDef.getParms().get(MONSTERID));
		}
		target.setValue(targetDef.getValue());
		return target;
	}

	@ReceiverAnno
	public void onKillMonster(KillMonsterEvent event) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("objectKey", event.getObjectKey());
		progress(event.getTrainer(), params);
	}

	@Override
	protected void doProgress(QuestTarget target, List<Quest> updateQuests, Quest quest, Map<String, Object> params) {
		if (!target.isComplete()) {
			Integer monsterId = Integer.valueOf(target.getParms().get("MONSTERID"));
			if (monsterId.intValue() == (int) params.get("objectKey")) {
				target.addCount();
				updateQuests.add(quest);
			}
		}
	}

}
