package com.mmorpg.qx.module.quest.model;

import com.mmorpg.qx.module.condition.ConditionType;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.quest.condition.QuestCompleteCondition;
import com.mmorpg.qx.module.quest.condition.QuestNoCompleteCondition;
import com.mmorpg.qx.module.quest.condition.QuestTodayCompleteCondition;
import com.mmorpg.qx.module.quest.resource.QuestResource;

/**
 * 任务类型
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
public enum QuestType {
	/** 主线 */
	TRUNK(1, "主线") {
		@Override
		public Conditions createConditions(QuestResource questResource) {
			Conditions andConditionList = new Conditions();
			// 历史未完成
			QuestNoCompleteCondition condition = ConditionType.QUEST_NOCOMPLETE.create();
			condition.setCode(questResource.getId() + "");
			condition.setValue(1);
			andConditionList.addCondition(condition);
			// 前置任务
			if (questResource.getNeedQuests() != null) {
				for (int questId : questResource.getNeedQuests()) {
					QuestCompleteCondition questCompleteCondition = ConditionType.QUEST_COMPLETE.create();
					questCompleteCondition.setCode(questId + "");
					questCompleteCondition.setValue(1);
					andConditionList.addCondition(questCompleteCondition);
				}
			}
			return andConditionList;
		}

	},
	/** 日常 */
	DAY(2, "日常") {
		@Override
		public Conditions createConditions(QuestResource questResource) {
			Conditions andConditionList = new Conditions();
			// 日常的接取条件是今日未完成
			// 这个条件提取到配置表里
			// andConditionList.addCondition(QuestNoTodayCompleteCondition.valueOf(questResource.getId(),
			// 1));
			// 前置任务
			if (questResource.getNeedQuests() != null) {
				for (int questId : questResource.getNeedQuests()) {
					QuestTodayCompleteCondition questTodayCompleteCondition = ConditionType.QUEST_TODAY_COMPLETE
							.create();
					questTodayCompleteCondition.setCode(questId + "");
					questTodayCompleteCondition.setValue(1);
					andConditionList.addCondition(questTodayCompleteCondition);
				}
			}
			return andConditionList;
		}

	},

	/** 支线 */
	BRANCH(3, "支线") {
		@Override
		public Conditions createConditions(QuestResource questResource) {
			Conditions andConditionList = new Conditions();
			// 历史未完成
			QuestNoCompleteCondition condition = ConditionType.QUEST_NOCOMPLETE.create();
			condition.setCode(questResource.getId() + "");
			condition.setValue(1);
			andConditionList.addCondition(condition);
			// 前置任务
			if (questResource.getNeedQuests() != null) {
				for (int questId : questResource.getNeedQuests()) {
					QuestCompleteCondition questCompleteCondition = ConditionType.QUEST_COMPLETE.create();
					questCompleteCondition.setCode(questId + "");
					questCompleteCondition.setValue(1);
					andConditionList.addCondition(questCompleteCondition);
				}
			}
			return andConditionList;
		}

	},;

	/**
	 * 根据值获取任务类型 默认为主线
	 * 
	 * @param value
	 * @return
	 */
	public static QuestType typeOf(int value) {
		switch (value) {
		case 1:
			return TRUNK;
		case 2:
			return DAY;
		case 3:
			return BRANCH;
		}
		return TRUNK;
	}

	private int value;

	private String desc;

	private QuestType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return this.value;
	}

	public abstract Conditions createConditions(QuestResource questResource);

}
