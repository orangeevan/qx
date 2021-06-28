package com.mmorpg.qx.module.quest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.entity.QuestEntity;
import com.mmorpg.qx.module.quest.packet.AcceptableQuestAddResp;
import com.mmorpg.qx.module.quest.packet.AcceptableQuestRemoveResp;
import com.mmorpg.qx.module.quest.packet.QuestUpdateResp;
import com.mmorpg.qx.module.quest.resource.QuestResource;
import com.mmorpg.qx.module.quest.service.QuestManager;
import com.mmorpg.qx.module.quest.target.QuestTarget;
import com.mmorpg.qx.module.quest.target.QuestTargetType;
import com.haipaite.common.utility.DateUtils;

/**
 * 任务集合
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
public class QuestBox {

	@Transient
	private Player owner;

	@Transient
	private QuestEntity questEntity;

	// private Logger logger = Logger.getLogger(QuestBox.class);

	/** 可接任务集合 */
	private Set<Integer> acceptableQuestIds = new HashSet<Integer>();
	/** 正在进行的任务集合 */
	private Map<Integer, Quest> acceptedQuests = new HashMap<Integer, Quest>();
	/** 历史任务各完成记录<任务id,完成次数> */
	private Map<Integer, Integer> completedCounts = new HashMap<Integer, Integer>();
	/** 今日各任务完成记录<任务id,完成次数> */
	private Map<Integer, Integer> todayCompletedCounts = new HashMap<Integer, Integer>();

	/** 日常任务最后刷新时间 */
	private long lastRefreshDailyQuestTime;

	private boolean update;

	public void serialize() {
		questEntity.setAcceptableQuestIds(JSON.toJSONString(acceptableQuestIds));
		questEntity.setAcceptedQuests(JSON.toJSONString(acceptedQuests));
		questEntity.setCompletedCounts(JSON.toJSONString(completedCounts));
		questEntity.setTodayCompletedCounts(JSON.toJSONString(todayCompletedCounts));
		questEntity.setLastRefreshDailyQuestTime(lastRefreshDailyQuestTime);
	}

	public QuestUpdateResp createVO() {
		QuestUpdateRecords update = new QuestUpdateRecords();
		for (Quest quest : acceptedQuests.values()) {
			update.addQuest(quest);
		}
		update.setAcceptableQuestIds(acceptableQuestIds);
		update.setCompletedCounts(completedCounts);
		update.setTodayCompletedCounts(todayCompletedCounts);
		return update.createVO();
	}

	public Quest getAcceptedTrunkQuest() {
		for (Quest quest : acceptedQuests.values()) {
			if (quest.isTrunk()) {
				return quest;
			}
		}
		return null;
	}

	public QuestEntity toEntity() {
		QuestEntity questEntity = new QuestEntity();
		questEntity.setAcceptableQuestIds(JSON.toJSONString(acceptableQuestIds));
		questEntity.setAcceptedQuests(JSON.toJSONString(acceptedQuests));
		questEntity.setCompletedCounts(JSON.toJSONString(completedCounts));
		questEntity.setTodayCompletedCounts(JSON.toJSONString(todayCompletedCounts));
		questEntity.setLastRefreshDailyQuestTime(lastRefreshDailyQuestTime);
		return questEntity;
	}

	public void parseFromEntity(QuestEntity questEntity) {
		this.acceptableQuestIds = JSON.parseObject(questEntity.getAcceptableQuestIds(),
				new TypeReference<Set<Integer>>() {
				});
		this.acceptedQuests = JSON.parseObject(questEntity.getAcceptedQuests(),
				new TypeReference<Map<Integer, Quest>>() {
				});
		this.completedCounts = JSON.parseObject(questEntity.getCompletedCounts(),
				new TypeReference<Map<Integer, Integer>>() {
				});
		this.todayCompletedCounts = JSON.parseObject(questEntity.getTodayCompletedCounts(),
				new TypeReference<Map<Integer, Integer>>() {
				});
		this.lastRefreshDailyQuestTime = questEntity.getLastRefreshDailyQuestTime();
	}

	/**
	 * 是否包含指定的可接任务
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasAcceptableQuest(int id) {
		return acceptableQuestIds.contains(id);
	}

	/**
	 * 增加一个可接任务
	 * 
	 * @param id
	 */
	public void addAcceptableQuest(int id) {
		if (!acceptableQuestIds.contains(id)) {
			acceptableQuestIds.add(id);
			update = true;
			QuestManager.getInstance().update(owner);
			AcceptableQuestAddResp resp = new AcceptableQuestAddResp();
			resp.setId(id);
			PacketSendUtility.sendPacket(owner, resp);
		}
	}

	/**
	 * 获取当前已接任务的id
	 * 
	 * @return
	 */
	@JSONField(serialize = false)
	public List<Integer> getAcceptedQuestIdsClone() {
		return new ArrayList<Integer>(getAcceptedQuests().keySet());
	}

	/**
	 * 根据任务目标类型获取未完成的任务列表
	 * 
	 * @param type
	 * @return
	 */
	@JSONField(serialize = false)
	public List<Quest> getInCompleteQuestsByQuestTargetType(QuestTargetType type) {
		List<Quest> quests = new ArrayList<Quest>();
		for (Quest quest : getAcceptedQuests().values()) {
			if (quest.getQuestPhase() == QuestPhase.PROGRESS) {
				for (QuestTarget questTarget : quest.getQuestTargets()) {
					if (questTarget.getQuestTargetType() == type) {
						quests.add(quest);
						break;
					}
				}
			}
		}
		return quests;
	}

	/**
	 * 获取指定的正在进行的任务
	 * 
	 * @param questId
	 * @return
	 */
	@JSONField(serialize = false)
	public Quest getAcceptedQuest(int id) {
		return getAcceptedQuests().get(id);
	}

	/**
	 * 是否正在做某项任务
	 * 
	 * @param questId
	 * @return
	 */
	@JSONField(serialize = false)
	public Quest getAcceptedQuest(int... ids) {
		for (int id : ids) {
			if (getAcceptedQuests().get(id) != null) {
				return getAcceptedQuests().get(id);
			}
		}
		return null;
	}

	/**
	 * 是否正在进行指定的任务
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasAcceptedQuest(int id) {
		return getAcceptedQuests().containsKey(id);
	}

	/**
	 * 添加一个正在进行的任务
	 * 
	 * @param quest
	 */
	public void addAcceptedQuest(Quest quest) {
		// 移除可接任务
		if (hasAcceptableQuest(quest.getQuestId())) {
			removeAcceptable(quest.getQuestId());
		}
		// 添加到任务列表
		getAcceptedQuests().put(quest.getQuestId(), quest);
		if (quest.isTrunk()) {
			questEntity.setTrunkQuestId(quest.getQuestId());
		}
	}

	/**
	 * 从可接任务中移除
	 * 
	 * @param questId
	 */
	public void removeAcceptable(int questId) {
		acceptableQuestIds.remove(questId);
		update = true;
		QuestManager.getInstance().update(owner);
		AcceptableQuestRemoveResp resp = new AcceptableQuestRemoveResp();
		resp.setId(questId);
		PacketSendUtility.sendPacket(owner, resp);
	}

	/**
	 * 移除一个正在进行的任务，或完成一个任务
	 * 
	 * @param id
	 * @return
	 */
	public Quest completeRemoveAcceptedQuest(int id, QuestUpdateRecords records) {
		Quest quest = removeAcceptedQuest(id);
		// 添加到完成记录,防止重复接任务
		addCount(todayCompletedCounts, id);
		addCount(completedCounts, id);
		records.getCompletedCounts().put(id, completedCounts.get(id));
		records.getTodayCompletedCounts().put(id, todayCompletedCounts.get(id));
		return quest;
	}

	public Quest removeAcceptedQuest(int id) {
		Quest quest = getAcceptedQuests().remove(id);
		return quest;
	}

	/**
	 * 添加任务完成记录
	 * 
	 * @param targets
	 * @param id
	 */
	private void addCount(Map<Integer, Integer> targets, int id) {
		Integer newCount = targets.get(id);
		if (newCount == null) {
			targets.put(id, 1);
		} else {
			targets.put(id, newCount.intValue() + 1);
		}
		QuestManager.getInstance().update(owner);
	}

	/**
	 * 该任务是否达到完成限制次数
	 * 
	 * @param questId
	 * @return
	 */
	@JSONField(serialize = false)
	public boolean isCompleted(Integer questId, int count) {
		if (!completedCounts.containsKey(questId)) {
			return false;
		}
		return completedCounts.get(questId) >= count;
	}

	@JSONField(serialize = false)
	public boolean isCompletedToday(Integer questId, int count) {
		if (!todayCompletedCounts.containsKey(questId)) {
			return false;
		}
		return todayCompletedCounts.get(questId) >= count;
	}

	/**
	 * 清除日常记录
	 */
	public void clearDaily() {
		if (!DateUtils.isToday(new Date(this.lastRefreshDailyQuestTime))) {
			lastRefreshDailyQuestTime = System.currentTimeMillis();
			todayCompletedCounts.clear();

			QuestUpdateRecords records = new QuestUpdateRecords();
			List<Integer> removes = new ArrayList<>();
			// 清理日常任务
			for (Quest quest : getAcceptedQuests().values()) {
				if (quest.getResource().getQuestType() == QuestType.DAY) {
					removes.add(quest.getQuestId());
				}
			}
			for (int removeId : removes) {
				getAcceptedQuests().remove(removeId);
				records.removeQuest(removeId);
			}
			records.sendTo(owner);
		}

	}

	/**
	 * 是否存在该任务类型
	 * 
	 * @param questType
	 * @return
	 */
	public boolean existQuestType(QuestType questType) {
		for (int questId : acceptableQuestIds) {
			QuestResource resource = QuestManager.getInstance().getQuestResource(questId);
			if (resource.getQuestType() == questType) {
				return true;
			}
		}
		for (int questId : getAcceptedQuests().keySet()) {
			QuestResource resource = QuestManager.getInstance().getQuestResource(questId);
			if (resource.getQuestType() == questType) {
				return true;
			}
		}
		return false;
	}

	public Quest getQuestByType(QuestType questType) {
		for (Quest quest : getAcceptedQuests().values()) {
			if (quest.getResource().getQuestType() == questType) {
				return quest;
			}
		}
		return null;
	}

	@JSONField(serialize = false)
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	// ---------get set-----------------

	public Map<Integer, Integer> getCompletedCounts() {
		return completedCounts;
	}

	public void setCompletedCounts(Map<Integer, Integer> completedCounts) {
		this.completedCounts = completedCounts;
	}

	public Map<Integer, Integer> getTodayCompletedCounts() {
		return todayCompletedCounts;
	}

	public void setTodayCompletedCounts(Map<Integer, Integer> todayCompletedCounts) {
		this.todayCompletedCounts = todayCompletedCounts;
	}

	public long getLastRefreshDailyQuestTime() {
		return lastRefreshDailyQuestTime;
	}

	public void setLastRefreshDailyQuestTime(long lastRefreshDailyQuestTime) {
		this.lastRefreshDailyQuestTime = lastRefreshDailyQuestTime;
	}

	public boolean hasComplete(int id) {
		return completedCounts.containsKey(id);
	}

	public Map<Integer, Quest> getAcceptedQuests() {
		return acceptedQuests;
	}

	public void setAcceptedQuests(Map<Integer, Quest> acceptedQuests) {
		this.acceptedQuests = acceptedQuests;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public QuestEntity getQuestEntity() {
		return questEntity;
	}

	public void setQuestEntity(QuestEntity questEntity) {
		this.questEntity = questEntity;
	}

}
