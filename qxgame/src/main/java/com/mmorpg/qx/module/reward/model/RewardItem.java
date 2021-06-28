package com.mmorpg.qx.module.reward.model;

import java.util.Map;

public class RewardItem {
	/** 奖励类型 */
	private RewardType type;
	/** 奖励编码 */
	private String code;
	/** 奖励数量 */
	private int amount;
	/** 额外参数 */
	private Map<String, String> parms;

	public static RewardItem valueOf(RewardType rewardType, String code, int amount, Map<String, String> parms) {
		RewardItem item = new RewardItem();
		item.type = rewardType;
		item.code = code;
		item.setAmount(amount);
		item.parms = parms;
		return item;
	}

	public RewardType getType() {
		return type;
	}

	public void setType(RewardType type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Map<String, String> getParms() {
		return parms;
	}

	public void setParms(Map<String, String> parms) {
		this.parms = parms;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
