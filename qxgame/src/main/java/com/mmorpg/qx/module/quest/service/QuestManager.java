package com.mmorpg.qx.module.quest.service;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityBuilder;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.ConditionType;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.entity.QuestEntity;
import com.mmorpg.qx.module.quest.model.Quest;
import com.mmorpg.qx.module.quest.model.QuestBox;
import com.mmorpg.qx.module.quest.model.QuestType;
import com.mmorpg.qx.module.quest.resource.QuestResource;
import com.mmorpg.qx.module.quest.target.QuestTarget;
import com.mmorpg.qx.module.quest.target.QuestTargetDef;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class QuestManager implements ResourceReload, EntityOfPlayerUpdateRule {

	public static Logger logger =  SysLoggerFactory.getLogger(QuestManager.class);

	@Static
	private Storage<Integer, QuestResource> questResources;

	@Inject
	private EntityCacheService<Long, QuestEntity> questEntityCache;

	private static QuestManager self;

	public static QuestManager getInstance() {
		return self;
	}

	/**
	 * 条件与任务的关联
	 */
	private Map<ConditionType, Set<Integer>> acceptConditionTypes = new HashMap<ConditionType, Set<Integer>>();

	private Map<ConditionType, Set<Integer>> removeConditionTypes = new HashMap<ConditionType, Set<Integer>>();

	@PostConstruct
	private void init() {
		self = this;
		initResources();
	}

	/**
	 * 创建任务
	 * 
	 * @param questId
	 * @return
	 */
	public Quest createQuest(Player player, int questId) {
		Quest quest = Quest.valueOf(questId, player);
		QuestResource questResource = getQuestResource(questId);
		// 创建任务目标
		if (questResource.getTargetDefs() != null) {
			for (QuestTargetDef targetDef : questResource.getTargetDefs()) {
				QuestTarget target = targetDef.getType().getQuestTargetHandler().create(targetDef, player);
				quest.getQuestTargets().add(target);
			}
		}
		return quest;
	}

	public QuestResource getQuestResource(int id) {
		return this.questResources.get(id, true);
	}

	public List<Integer> getAllQuestIds() {
		List<Integer> allQuestIds = new ArrayList<Integer>();
		for (QuestResource resource : questResources.getAll()) {
			allQuestIds.add(resource.getId());
		}
		return allQuestIds;
	}

	public boolean isDayQuest(int questId) {
		QuestResource resource = questResources.get(questId, true);
		if (resource == null) {
			return false;
		}
		return resource.getQuestType() == QuestType.DAY;
	}

	public boolean isTrunkQuest(int questId) {
		QuestResource resource = questResources.get(questId, true);
		if (resource == null) {
			return false;
		}
		return resource.getQuestType() == QuestType.TRUNK;
	}

	public Map<ConditionType, Set<Integer>> getAcceptConditionTypes() {
		return acceptConditionTypes;
	}

	public Map<ConditionType, Set<Integer>> getRemoveConditionTypes() {
		return removeConditionTypes;
	}

	private void initResources(){
		acceptConditionTypes.clear();
		removeConditionTypes.clear();
		for (QuestResource questResource : questResources.getAll()) {
			Conditions accpetConditions = questResource.getAcceptCondition();
			for (AbstractCondition condition : accpetConditions.getConditionList()) {
				if (!acceptConditionTypes.containsKey(condition.getType())) {
					acceptConditionTypes.put(condition.getType(), new HashSet<Integer>());
				}
				acceptConditionTypes.get(condition.getType()).add(questResource.getId());
			}

			Conditions removeConditions = questResource.getRemoveCondition();
			for (AbstractCondition condition : removeConditions.getConditionList()) {
				if (!removeConditionTypes.containsKey(condition.getType())) {
					removeConditionTypes.put(condition.getType(), new HashSet<Integer>());
				}
				removeConditionTypes.get(condition.getType()).add(questResource.getId());
			}

		}
	}

	@Override
	public void reload() {
		initResources();
	}

	@Override
	public Class<?> getResourceClass() {
		return QuestResource.class;
	}

	@Override
	public void initPlayer(Player player) {
		QuestEntity questEntity = questEntityCache.loadOrCreate(player.getObjectId(),
				new EntityBuilder<Long, QuestEntity>() {
					@Override
					public QuestEntity newInstance(Long id) {
						QuestBox questBox = new QuestBox();
						QuestEntity pe = questBox.toEntity();
						pe.setPlayerId(id);
						return pe;
					}
				});
		QuestBox questBox = new QuestBox();
		questBox.parseFromEntity(questEntity);
		questBox.setOwner(player);
		questBox.setQuestEntity(questEntity);
		player.setQuestBox(questBox);
	}

	@Override
	public void update(Player player) {
		player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.QUEST_ENTITY_UPDATE, new AbstractDispatcherHashCodeRunable() {
			@Override
			public String name() {
				return "QUEST_ENTITY_UPDATE";
			}

			@Override
			public int getDispatcherHashCode() {
				return player.getDispatcherHashCode();
			}

			@Override
			public void doRun() {
				logoutWriteBack(player);
			}
		}, 1);
	}

	@Override
	public void logoutWriteBack(Player player) {
		player.getQuestBox().serialize();
		questEntityCache.writeBack(player.getObjectId(), player.getQuestBox().getQuestEntity());
	}


}
