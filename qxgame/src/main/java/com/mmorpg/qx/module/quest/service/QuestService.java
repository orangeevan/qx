package com.mmorpg.qx.module.quest.service;

import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.utility.New;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.condition.ConditionType;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.event.QuestRewardEvent;
import com.mmorpg.qx.module.quest.model.Quest;
import com.mmorpg.qx.module.quest.model.QuestBox;
import com.mmorpg.qx.module.quest.model.QuestPhase;
import com.mmorpg.qx.module.quest.model.QuestUpdateRecords;
import com.mmorpg.qx.module.quest.packet.RewardQuestResp;
import com.mmorpg.qx.module.quest.resource.QuestResource;
import com.mmorpg.qx.module.reward.manager.RewardManager;
import com.mmorpg.qx.module.reward.model.Reward;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QuestService {

    public static Logger logger = SysLoggerFactory.getLogger(QuestService.class);

    @Autowired
    private RewardManager rewardManager;

    @Autowired
    private QuestManager questManager;

    private void filterAndAcceptQuests(Player player, Collection<Integer> questIds, QuestUpdateRecords records) {
        Collection<Integer> acceptableQuestIds = findAcceptableQuests(player, questIds);

        // 尝试接受任务
        for (int questId : acceptableQuestIds) {
            tryAcceptQuest(player, questId, records);
        }

    }

    /**
     * 从指定的任务列表中查找可接取的任务，可接任务需要满足接取条件
     *
     * @param player
     * @param questIds
     * @return
     */
    private Collection<Integer> findAcceptableQuests(Player player, Collection<Integer> questIds) {

        List<Integer> acceptableQuestIds = New.arrayList();
        // 过滤任务
        for (int questId : questIds) {
            QuestResource resource = questManager.getQuestResource(questId);
            if (resource == null) {
                continue;
            }
            // 已经接取这个任务
            if (player.getQuestBox().hasAcceptedQuest(questId)) {
                continue;
            }
            acceptableQuestIds.add(questId);
        }

        // 检查是否满足接受条件
        Iterator<Integer> iter = acceptableQuestIds.iterator();
        while (iter.hasNext()) {
            int questId = iter.next();
            QuestResource resource = questManager.getQuestResource(questId);
            Result result = resource.getAcceptCondition().verify(player, null, 1);
            if (result.isSuccess()) {
                if (!resource.isAutoAccept()) {
                    player.getQuestBox().addAcceptableQuest(questId);
                }
            } else {
                if (player.getQuestBox().hasAcceptableQuest(questId)) {
                    player.getQuestBox().removeAcceptable(questId);
                }
                iter.remove();
            }
        }
        return acceptableQuestIds;
    }

    public void acceptQuest(Player player, int questId) {
        // 没有在可接列表中
        if (!player.getQuestBox().hasAcceptableQuest(questId)) {
            throw new ManagedException(ManagedErrorCode.QUEST_CANNOT_ACCEPT);
        }

        QuestResource questResource = questManager.getQuestResource(questId);
        if (questResource.getAcceptNPC() != 0) {
            checkNpcInSight(player, questResource.getAcceptNPC());
        }

        QuestUpdateRecords records = new QuestUpdateRecords();
        acceptQuest0(player, questId, records);
        records.sendTo(player);
    }

    /**
     * 尝试接受任务，需要在可接取列表中，并且可以自动接取
     *
     * @param player
     * @param questId
     */
    private boolean tryAcceptQuest(Player player, int questId, QuestUpdateRecords records) {
        QuestResource resource = questManager.getQuestResource(questId);
        if (!resource.isAutoAccept()) {
            return false;
        }
        acceptQuest0(player, questId, records);
        return true;
    }

    /**
     * 直接接受任务
     *
     * @param player
     * @param questId
     */
    private void acceptQuest0(Player player, int questId, QuestUpdateRecords records) {
        Quest quest = questManager.createQuest(player, questId);
        player.getQuestBox().addAcceptedQuest(quest);

        records.addQuest(quest);
        // QuestResource questResource = questManager.getQuestResource(questId);
        // Context.getTlogService().mainQuestAccept(player, questResource);
        if (canCompleteQuest(player, quest)) {
            completeQuest0(player, quest, records);
        }
    }

    public void updateQuestProgress(Player player, List<Quest> quests) {
        if (quests.isEmpty()) {// 不存在任务有进度更新
            return;
        }
        // 尝试完成任务
        QuestUpdateRecords records = new QuestUpdateRecords();
        for (Quest quest : quests) {
            records.addQuest(quest);
            if (canCompleteQuest(player, quest)) {
                completeQuest0(player, quest, records);
            }
        }
        records.sendTo(player);

    }

    public void completeQuest(Player player, int questId) {

        QuestResource resource = questManager.getQuestResource(questId);
        if (!resource.isClientComplete()) {
            throw new ManagedException(ManagedErrorCode.QUEST_NOT_CLIENT_COMPLETE);
        }
        Quest quest = player.getQuestBox().getAcceptedQuest(questId);
        if (quest == null || quest.getQuestPhase() != QuestPhase.PROGRESS) {
            // 任务未接取或者任务已完成
            throw new ManagedException(ManagedErrorCode.QUEST_NOT_ACCEPTED_OR_COMPLETE);

        }

        QuestUpdateRecords records = new QuestUpdateRecords();
        completeQuest0(player, quest, records);// 完成任务
        records.sendTo(player);

    }

    public boolean canCompleteQuest(Player player, Quest quest) {
        QuestResource resource = questManager.getQuestResource(quest.getQuestId());
        if (resource.isClientComplete()) {
            return false;
        }
        if (!quest.checkComplete()) {
            return false;
        }
        return true;
    }

    /**
     * 直接完成任务
     *
     * @param player
     * @param quest
     */
    private void completeQuest0(Player player, Quest quest, QuestUpdateRecords records) {
        quest.setQuestPhase(QuestPhase.COMPLETE);
        records.addQuest(quest);
        tryReward(player, quest.getQuestId(), records);
    }

    private void checkNpcInSight(Player player, int npcId) {
//		if (player.getKnownList().knownObjectKey(npcId)) {
//			throw new ManagedException(ManagedErrorCode.TASK_NPC_DISTANCE_TOO_LONG);
//		}
    }

    public void reward(Player player, int questId) {

        Quest quest = player.getQuestBox().getAcceptedQuest(questId);
        if (quest == null) {
            // 任务未接取
            throw new ManagedException(ManagedErrorCode.QUEST_NOT_FOUND);
        }
        if (quest.getQuestPhase() == QuestPhase.PROGRESS) {
            throw new ManagedException(ManagedErrorCode.QUEST_INCOMPLETE);
        }

        QuestResource questResource = questManager.getQuestResource(questId);
        if (questResource.getCommitNPC() != 0) {
            checkNpcInSight(player, questResource.getCommitNPC());
        }

        QuestUpdateRecords records = new QuestUpdateRecords();
        reward0(player, quest, records, 1);
        records.sendTo(player);

    }

    private boolean tryReward(Player player, int questId, QuestUpdateRecords records) {
        QuestResource resource = questManager.getQuestResource(questId);
        if (!resource.isAutoReward()) {
            return false;
        }
        Quest quest = player.getQuestBox().getAcceptedQuest(questId);
        if (quest == null) {
            return false;
        }

        reward0(player, quest, records, 1);
        return true;

    }

    /**
     * 直接领奖
     *
     * @param player
     * @param quest
     * @param multiple
     */
    private void reward0(Player player, Quest quest, QuestUpdateRecords records, int multiple) {
        QuestBox questBox = player.getQuestBox();

        records.removeQuest(quest.getQuestId());
        QuestResource questResource = quest.getResource();

        // 发放奖励
        Reward reward = RewardManager.creatReward(1, questResource.getRewards());

        rewardManager.grantReward(player, reward,
                ModuleInfo.valueOf(ModuleType.QUEST, SubModuleType.SHOP_BUY, "" + questResource.getId()));

        // 通知前端领奖成功
        PacketSendUtility.sendPacket(player, RewardQuestResp.valueOf(quest.getQuestId()));

        // 记录完成次数
        questBox.completeRemoveAcceptedQuest(quest.getQuestId(), records);

        // 触发模块开启事件
        if (questResource.getModuleId() != 0) {
            QuestRewardEvent event = QuestRewardEvent.valueOf(player, quest.getQuestId());
            EventBusManager.getInstance().submit(event);
        }

        // 接受下一个任务
        acceptNextQuest(player, quest.getQuestId(), records);

    }

    /**
     * 接受下一个任务
     *
     * @param player
     * @param questId
     */
    private void acceptNextQuest(Player player, int questId, QuestUpdateRecords records) {
        List<Integer> nextQuestIds = findNextQuestIds(player, questId);
        filterAndAcceptQuests(player, nextQuestIds, records);
    }

    /**
     * 查找下一个任务
     *
     * @param player
     * @param questId
     * @return
     */
    private List<Integer> findNextQuestIds(Player player, int questId) {
        List<Integer> newQuestIds = new ArrayList<Integer>();
        QuestResource questResource = questManager.getQuestResource(questId);

        List<Integer> childQuests = questResource.getChildQuests();
        if (childQuests != null && childQuests.size() != 0) {
            for (int id : childQuests) {
                newQuestIds.add(id);
            }
        }

        return newQuestIds;
    }

    /**
     * 尝试移除任务，需要达到移除的条件
     *
     * @param player
     * @param questId
     * @return
     */
    private boolean needRemoveAcceptedQuest(Player player, int questId) {
        if (!player.getQuestBox().hasAcceptedQuest(questId)) {
            return false;
        }
        QuestResource resource = questManager.getQuestResource(questId);
        Result resultCode = resource.getRemoveCondition().verify(player, null, 1);
        if (!resultCode.isSuccess()) {
            return false;
        }

        return true;
    }

    private void removeAcceptedQuest(Player player, int questId, QuestUpdateRecords records) {
        Quest quest = player.getQuestBox().removeAcceptedQuest(questId);
        if (quest == null) {
            return;
        }

        if (records != null) {
            records.removeQuest(quest.getQuestId());
        }
    }

    public void refreshQuests(Player player) {
        QuestUpdateRecords records = new QuestUpdateRecords();
        // QuestBox questBox = player.getQuestBox();

        // 移除任务
        tryRemoveAcceptedQuests(player, records);

        // 完成任务
        tryCompleteQuests(player, records);

        // 接取任务
        List<Integer> tryAcceptQuestIds = new ArrayList<>();
        List<Integer> allQuestIds = questManager.getAllQuestIds();
        for (int questId : allQuestIds) {
            if (!player.getQuestBox().hasComplete(questId) || questManager.isDayQuest(questId)) {
                tryAcceptQuestIds.add(questId);
            }
        }
        filterAndAcceptQuests(player, tryAcceptQuestIds, records);

        records.sendTo(player);

    }

    private void tryCompleteQuests(Player player, QuestUpdateRecords records) {
        List<Quest> completeQuests = new ArrayList<Quest>();
        for (Quest quest : player.getQuestBox().getAcceptedQuests().values()) {
            if (canCompleteQuest(player, quest)) {
                completeQuests.add(quest);
            }
        }
        if (!completeQuests.isEmpty()) {
            for (Quest quest : completeQuests) {
                completeQuest0(player, quest, records);
            }
        }
    }

    private void tryRemoveAcceptedQuests(Player player, QuestUpdateRecords records) {
        List<Integer> removeQuestIds = new ArrayList<Integer>();
        for (int questId : player.getQuestBox().getAcceptedQuests().keySet()) {
            if (needRemoveAcceptedQuest(player, questId)) {
                removeQuestIds.add(questId);
            }
        }
        if (!removeQuestIds.isEmpty()) {
            for (int questId : removeQuestIds) {
                removeAcceptedQuest(player, questId, records);
            }
        }
    }

    public void refreshQuestsByConditionType(Player player, ConditionType... refreshTypes) {
        Set<ConditionType> types = new HashSet<>();
        for (ConditionType type : refreshTypes) {
            types.add(type);
        }
        refreshQuestsByConditionType(player, types);
    }

    public void refreshQuestsByConditionType(Player player, Set<ConditionType> refreshTypes) {
        QuestUpdateRecords records = new QuestUpdateRecords();

        // 移除任务
        tryRemoveAcceptedQuests(player, records);

        Set<Integer> acceptQuestIds = new HashSet<>();
        for (ConditionType conditionType : refreshTypes) {
            if (questManager.getAcceptConditionTypes().containsKey(conditionType)) {
                for (int questId : questManager.getAcceptConditionTypes().get(conditionType)) {
                    acceptQuestIds.add(questId);
                }
            }
        }
        // 接取任务
        filterAndAcceptQuests(player, acceptQuestIds, records);

        records.sendTo(player);
    }

    public void gmReward(Player player, int toQuestId) {
        QuestBox questBox = player.getQuestBox();
        QuestUpdateRecords records = new QuestUpdateRecords();

        Quest trunkQuest = findTrunkQuest(questBox.getAcceptedQuests());
        if (trunkQuest == null && !player.getQuestBox().hasAcceptedQuest(1) && !questBox.isCompleted(1, 1)) {
            // 如果玩家首个任务都没有接取,就自动接取
            acceptQuest0(player, 1, records);
            trunkQuest = findTrunkQuest(questBox.getAcceptedQuests());
        }
        // 之后的走任务流程，忽略任务目标完成任务
        while (trunkQuest != null && !questBox.isCompleted(toQuestId, 1)) {
            if (trunkQuest.getQuestPhase() != QuestPhase.COMPLETE) {
                completeQuest0(player, questBox.getAcceptedQuest(trunkQuest.getQuestId()), records);
            }

            if (!questBox.isCompleted(trunkQuest.getQuestId(), 1)) {
                reward0(player, questBox.getAcceptedQuest(trunkQuest.getQuestId()), records, 1);
            }

            for (int questId : trunkQuest.getResource().getChildQuests()) {
                if (!questBox.hasAcceptedQuest(questId) && questManager.isTrunkQuest(questId)) {
                    acceptQuest0(player, questId, new QuestUpdateRecords());
                }
            }

            trunkQuest = findTrunkQuest(questBox.getAcceptedQuests());
            if (trunkQuest != null && questBox.isCompleted(trunkQuest.getQuestId(), 1)) {
                throw new RuntimeException("已经完成该主线：" + trunkQuest.getQuestId());
            }

        }
        records.sendTo(player);
    }

    /**
     * 查找当前主线任务（最多只有一个）
     *
     * @param currentQuestMaps
     * @return
     */
    private Quest findTrunkQuest(Map<Integer, Quest> currentQuestMaps) {
        for (Quest quest : currentQuestMaps.values()) {
            if (questManager.isTrunkQuest(quest.getQuestId())) {
                return quest;
            }
        }
        return null;
    }

    /**
     * 任务进行中传送 2015/10/15
     *
     * @param targetSceneId ,怪物id
     */
    public void teleportTaskScene(final Player player, int targetSceneId, int monsterId) {
		/*
		int sceneId = player.getSceneId();
		if (sceneId == targetSceneId) {
			return;
		}
		int lineId = player.getScene().getLineId();
		Point point = null;
		Scene tarScene = Context.getSceneService().getNormalScene(targetSceneId, lineId);
		Map<Long, Monster> monsters = tarScene.getAllMonsters();
		if (CollectionUtil.isEmpty(monsters)) {
			return;
		}
		if (!Context.getSceneService().isCanChangeScene(player)) {
			return;
		}
		for (Monster monster : monsters.values()) {
			if (monster.getConfig().getID() == monsterId) {
				if (monster.getCoord() != null) {
					Collection<Grid> grids = GridUtils.getByRadius(tarScene, monster.getCoord(), 1);
					for (Grid grid : grids) {
						if (grid.isEmpty()) {
							point = grid.getCenterPoint();
							break;
						}
					}
				}
			}
		}
		player.tryChangeScene(tarScene.getSceneId(), point, TransportType.TASK);
		*/
    }

}
