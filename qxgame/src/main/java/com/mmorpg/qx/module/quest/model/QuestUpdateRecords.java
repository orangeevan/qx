package com.mmorpg.qx.module.quest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.packet.QuestUpdateResp;
import com.mmorpg.qx.module.quest.packet.vo.PairEntry;
import com.mmorpg.qx.module.quest.packet.vo.QuestVO;
import com.mmorpg.qx.module.quest.service.QuestManager;

/**
 * 任务更新
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
public class QuestUpdateRecords {

	private Map<Integer, Quest> updatedQuests = new HashMap<>();

	/** 历史任务各完成记录<任务id,完成次数> */
	private Map<Integer, Integer> completedCounts = new HashMap<Integer, Integer>();
	/** 今日各任务完成记录<任务id,完成次数> */
	private Map<Integer, Integer> todayCompletedCounts = new HashMap<Integer, Integer>();

	private Set<Integer> acceptableQuestIds;

	public QuestUpdateResp createVO() {
		QuestUpdateResp sm = new QuestUpdateResp();
		sm.setCurrentQuests(new ArrayList<QuestVO>());
		List<Integer> removeList = new ArrayList<Integer>();
		for (Entry<Integer, Quest> entry : updatedQuests.entrySet()) {
			if (entry.getValue() == null) {
				removeList.add(entry.getKey());
			} else {
				sm.getCurrentQuests().add(entry.getValue().createVO());
			}
		}
		if (!removeList.isEmpty()) {
			sm.setRemoves(removeList);
		}
		if (!completedCounts.isEmpty()) {
			if (sm.getCompletionHistory() == null) {
				sm.setCompletionHistory(new ArrayList<>());
			}
			for (Entry<Integer, Integer> entry : completedCounts.entrySet()) {
				sm.getCompletionHistory().add(PairEntry.valueOf(entry.getKey(), entry.getValue()));
			}
		}

		if (!todayCompletedCounts.isEmpty()) {
			if (sm.getTodayCompletionHistory() == null) {
				sm.setTodayCompletionHistory(new ArrayList<>());
			}
			for (Entry<Integer, Integer> entry : todayCompletedCounts.entrySet()) {
				sm.getTodayCompletionHistory().add(PairEntry.valueOf(entry.getKey(), entry.getValue()));
			}
		}
		if (acceptableQuestIds != null && !acceptableQuestIds.isEmpty()) {
			sm.setClientAccepts(new ArrayList<>(acceptableQuestIds));
		}
		return sm;
	}

	public void addQuests(Collection<Quest> quests) {
		for (Quest quest : quests) {
			updatedQuests.put(quest.getQuestId(), quest);
		}
	}

	public void addQuest(Quest quest) {
		updatedQuests.put(quest.getQuestId(), quest);
	}

	public void removeQuest(int id) {
		updatedQuests.put(id, null);
	}

	public boolean isEmpty() {
		return updatedQuests.isEmpty() && completedCounts.isEmpty() && todayCompletedCounts.isEmpty();
	}

	public void sendTo(Player player) {
		if (isEmpty()) {
			return;
		}
		QuestUpdateResp resp = this.createVO();
		player.getQuestBox().setUpdate(false);
		QuestManager.getInstance().update(player);
		PacketSendUtility.sendPacket(player, resp);
	}

	/***
	 * 删除这条任务的修改记录
	 * 
	 * @param id
	 */
	public void delete(int id) {
		updatedQuests.remove(id);
	}

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

	public Set<Integer> getAcceptableQuestIds() {
		return acceptableQuestIds;
	}

	public void setAcceptableQuestIds(Set<Integer> acceptableQuestIds) {
		this.acceptableQuestIds = acceptableQuestIds;
	}

}
