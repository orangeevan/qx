package com.mmorpg.qx.module.quest.target;

import com.haipaite.common.utility.New;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.model.Quest;
import com.mmorpg.qx.module.quest.service.QuestService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务目标处理器
 */
public abstract class QuestTargetHandler {

	protected Logger logger = SysLoggerFactory.getLogger(QuestTargetHandler.class);

	private static Map<QuestTargetType, QuestTargetHandler> handlers = new HashMap<QuestTargetType, QuestTargetHandler>();

	static QuestTargetHandler getHandler(QuestTargetType type) {
		return handlers.get(type);
	}

	@Autowired
	public QuestService questService;

	@PostConstruct
	public void init() {
		if (handlers.containsKey(getType())) {
			throw new RuntimeException(String.format("questTargetHandler type[%s]重复!", getType()));
		}
		handlers.put(getType(), this);
	}

	/**
	 * 获取当前类型
	 * 
	 * @return
	 */
	public abstract QuestTargetType getType();

	/***
	 * 创建任务条件
	 * 
	 * @param targetDef
	 * @param player
	 * @return
	 */
	public abstract QuestTarget create(QuestTargetDef targetDef, Player player);

	protected void progress(AbstractTrainerCreature trainer, Map<String, Object> params) {
		try {
			if(!(trainer instanceof PlayerTrainerCreature)){
				return;
			}
			Player player = ((PlayerTrainerCreature) trainer).getOwner();
			List<Quest> quests = player.getQuestBox().getInCompleteQuestsByQuestTargetType(getType());
			if (!quests.isEmpty()) {
				List<Quest> updateQuests = New.arrayList();
				for (Quest quest : quests) {
					for (QuestTarget target : quest.getQuestTargets()) {
						doProgress(target, updateQuests, quest, params);
					}
				}

				questService.updateQuestProgress(player, updateQuests);
			}
		} catch (Exception e) {
			logger.error("更新任务进度异常", e);
		}
	}

	protected abstract void doProgress(QuestTarget target, List<Quest> updateQuests, Quest quest,
			Map<String, Object> params);

}
