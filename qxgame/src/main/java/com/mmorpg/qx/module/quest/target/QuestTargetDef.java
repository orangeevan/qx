package com.mmorpg.qx.module.quest.target;

import java.util.HashMap;
import java.util.Map;

/**
 * 装备目标配置
 * 
 * @author wang ke
 * @since v1.0 2017年1月5日
 * 
 */
public class QuestTargetDef {

	/** 目标类型 */
	private QuestTargetType type;
	/** 目标参数 */
	private Map<String, String> parms = new HashMap<String, String>();
	/** 目标值 */
	private int value;

	public QuestTargetType getType() {
		return type;
	}

	public void setType(QuestTargetType type) {
		this.type = type;
	}

	public Map<String, String> getParms() {
		return parms;
	}

	public void setParms(Map<String, String> parms) {
		this.parms = parms;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
