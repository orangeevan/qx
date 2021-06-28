package com.mmorpg.qx.module.quest.target;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.quest.packet.vo.QuestTargetVO;

/**
 * 
 * 
 * @author wang ke
 * @since v1.0 2016年12月29日
 * 
 */
public class QuestTarget {

	/** 任务目标类型 */
	private QuestTargetType questTargetType;
	/** 条件参数 */
	private Map<String, String> parms = new HashMap<String, String>();
	/** 任务进度值 */
	private int count;
	/** 任务目标值 */
	private int value;

	public QuestTargetVO createVO() {
		QuestTargetVO vo = new QuestTargetVO();
		vo.setValue(count);
		vo.setType(questTargetType.getValue());
		return vo;
	}

	/***
	 * 任务目标是否达到
	 * 
	 * @return
	 */
	@JSONField(serialize = false)
	public boolean isComplete() {
		return count >= value;
	}

	@JSONField(serialize = false)
	public void addCount() {
		count += 1;
	}

	// --------------get set---------------

	public QuestTargetType getQuestTargetType() {
		return questTargetType;
	}

	public void setQuestTargetType(QuestTargetType questTargetType) {
		this.questTargetType = questTargetType;
	}

	public Map<String, String> getParms() {
		return parms;
	}

	public void setParms(Map<String, String> parms) {
		this.parms = parms;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
