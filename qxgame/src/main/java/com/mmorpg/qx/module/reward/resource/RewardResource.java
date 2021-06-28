package com.mmorpg.qx.module.reward.resource;

import java.util.Map;

import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardType;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * 
 * @author wang ke
 * @since v1.0 2018年3月9日
 *
 */
@Resource
public class RewardResource {
	@Id
	private int key;
	/** 奖励类型 */
	private RewardType type;
	/** 奖励编码 */
	private String code;
	/** 奖励数量 */
	private int amount;
	/** 额外参数 */
	private Map<String, String> parms;

	public RewardItem createRewardItem() {
		RewardItem item = new RewardItem();
		item.setAmount(amount);
		item.setCode(code);
		item.setParms(parms);
		item.setType(type);
		return item;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Map<String, String> getParms() {
		return parms;
	}

	public void setParms(Map<String, String> parms) {
		this.parms = parms;
	}

}
