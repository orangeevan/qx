package com.mmorpg.qx.module.quest.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.packet.vo.QuestTargetVO;
import com.mmorpg.qx.module.quest.packet.vo.QuestVO;
import com.mmorpg.qx.module.quest.resource.QuestResource;
import com.mmorpg.qx.module.quest.service.QuestManager;
import com.mmorpg.qx.module.quest.target.QuestTarget;
import com.haipaite.common.utility.New;

/**
 * 
 * 任务
 * 
 * @author wang ke
 * @since v1.0 2016年12月29日
 * 
 */
public class Quest {
	/** 任务id */
	private int questId;
	/** 任务状态 */
	private QuestPhase questPhase;
	/** 创建时间 */
	private long createTime;
	/** 任务条件的集合 **/
	private List<QuestTarget> questTargets;

	/**
	 * 
	 * 
	 * @param questId
	 * @param player
	 * @return
	 */
	public static Quest valueOf(int questId, Player player) {
		Quest quest = new Quest();
		quest.questTargets = New.arrayList();
		quest.createTime = System.currentTimeMillis();
		quest.questId = questId;
		quest.questPhase = QuestPhase.PROGRESS;
		return quest;
	}

	@JSONField(serialize = false)
	public boolean isProgress() {
		return questPhase == QuestPhase.PROGRESS;
	}

	/**
	 * 创建questVO
	 * 
	 * @return
	 */
	public QuestVO createVO() {
		QuestVO vo = new QuestVO();
		if (questTargets != null && !questTargets.isEmpty()) {
			vo.setTargets(new ArrayList<QuestTargetVO>());
			for (QuestTarget target : questTargets) {
				vo.getTargets().add(target.createVO());
			}
		}
		vo.setId(questId);
		vo.setPhase(questPhase.getValue());
		vo.setCreateTime(createTime);
		return vo;

	}

	/***
	 * 判断任务是否满足达成目标,并且任务未完成
	 * 
	 * @return
	 */
	@JSONField(serialize = false)
	public boolean checkComplete() {
		for (QuestTarget questKey : questTargets) {
			if (!questKey.isComplete()) {
				return false;
			}
		}
		if (getQuestPhase() != QuestPhase.PROGRESS) {
			return false;
		}
		return true;
	}

	@JSONField(serialize = false)
	public QuestResource getResource() {
		return QuestManager.getInstance().getQuestResource(questId);
	}

	@JSONField(serialize = false)
	public boolean isTrunk() {
		return getResource().getQuestType() == QuestType.TRUNK;
	}

	public QuestPhase getQuestPhase() {
		return questPhase;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	public void setQuestPhase(QuestPhase questPhase) {
		this.questPhase = questPhase;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public List<QuestTarget> getQuestTargets() {
		return questTargets;
	}

	public void setQuestTargets(List<QuestTarget> questTargets) {
		this.questTargets = questTargets;
	}

}
