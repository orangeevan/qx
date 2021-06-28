package com.mmorpg.qx.module.quest.target;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.model.Quest;

public class QuestTargetUtils {

	public static final QuestTarget buildEnterSceneQuestTarget(QuestTargetDef targetDef, Player player,
			QuestTargetType type, String sceneKey) {
		QuestTarget target = new QuestTarget();
		target.setQuestTargetType(type);
		if (targetDef.getParms() != null) {
			target.setParms(new HashMap<String, String>());
			target.getParms().put(sceneKey, targetDef.getParms().get(sceneKey));
		}
		String[] sceneIds = target.getParms().get(sceneKey).split(",");
		List<String> sceneList = Arrays.asList(sceneIds);
//		// 在当前场景，直接完成
//		if (sceneList.contains(player.getMapId() + "")) {
//			target.addCount();
//		}
		target.setValue(targetDef.getValue());
		return target;
	}

	public static final void updateEnterSceneProgress(QuestTarget target, List<Quest> updateQuests, Quest quest,
			Map<String, Object> params, String sceneKey) {
		if (!target.isComplete()) {
			String[] sceneIds = target.getParms().get(sceneKey).split(",");
			List<String> sceneList = Arrays.asList(sceneIds);
			if (sceneList.contains(params.get(sceneKey) + "")) {
				target.addCount();
				updateQuests.add(quest);
			}
		}

	}
}
