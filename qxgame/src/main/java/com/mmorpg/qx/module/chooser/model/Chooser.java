package com.mmorpg.qx.module.chooser.model;

import java.util.List;

import com.mmorpg.qx.module.chooser.manager.ChooserManager;

/**
 * 选择器,这个对象可以直接以json的形式直接序列化到配置表里
 * @author wang ke
 * @since v1.0 2018年3月26日
 *
 */
public class Chooser {
	/**
	 * 选择器类型
	 */
	private ChooserType type;
	/**
	 * 筛选的结果数量
	 */
	private int resultCount = 1;

	/**
	 * 条件筛选器需要用到的
	 */
	private transient Object conditionTarget;
	/**
	 * 选择内容
	 */
	private List<ChooserItem> items;

	/**
	 * 筛选出结果
	 * @return
	 */
	public List<ChooserItem> execute() {
		return ChooserManager.getInstance().chooser(this);
	}

	public ChooserType getType() {
		return type;
	}

	public void setType(ChooserType type) {
		this.type = type;
	}

	public List<ChooserItem> getItems() {
		return items;
	}

	public void setItems(List<ChooserItem> items) {
		this.items = items;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public Object getConditionTarget() {
		return conditionTarget;
	}

	public void setConditionTarget(Object conditionTarget) {
		this.conditionTarget = conditionTarget;
	}

}
