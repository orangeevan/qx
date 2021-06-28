package com.mmorpg.qx.module.chooser.model;

import com.mmorpg.qx.module.condition.resource.ConditionResource;

/**
 * 选择器选项
 * @author wang ke
 * @since v1.0 2018年3月26日
 *
 */
public class ChooserItem {
	/**
	 * 权重
	 */
	private int weight;
	/**
	 * 条件
	 */
	private ConditionResource condition;

	/**
	 * 结果值
	 */
	private String result;

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public ConditionResource getCondition() {
		return condition;
	}

	public void setCondition(ConditionResource condition) {
		this.condition = condition;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
