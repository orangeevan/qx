package com.mmorpg.qx.module.reward.model;

/**
 * 奖励类型
 * @author wang ke
 * @since v1.0 2018年3月9日
 *
 */
public enum RewardType {
	/** 货币 */
	PURSE(1),
	/** 道具 */
	ITEM(2),
	/** 经验 */
	EXP(3),
	/** 积分 */
	INTEGRAL(4);

	private int key;

	private RewardType(int key) {
		this.setKey(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
}
