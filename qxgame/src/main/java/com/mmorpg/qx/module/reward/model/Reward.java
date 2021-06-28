package com.mmorpg.qx.module.reward.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 奖励
 * @author wang ke
 * @since v1.0 2018年3月6日
 *
 */
public class Reward {

	private List<RewardItem> rewardItems = new ArrayList<>();

	public List<RewardItem> getRewardItems() {
		return rewardItems;
	}

	public void setRewardItems(List<RewardItem> rewardItems) {
		this.rewardItems = rewardItems;
	}

	@JSONField(serialize = false)
	public void multipleRewards(int multiValue) {
		for (RewardItem item : rewardItems) {
			item.setAmount(item.getAmount() * multiValue);
		}
	}

	/**
	 * 克隆自己,产生一个新的reward
	 * 
	 * @return
	 */
	@JSONField(serialize = false)
	public Reward copy() {
		Reward reward = new Reward();
		for (RewardItem rewardItem : rewardItems) {
			reward.rewardItems.add(RewardItem.valueOf(rewardItem.getType(), rewardItem.getCode(),
					rewardItem.getAmount(), rewardItem.getParms()));
		}
		return reward;
	}

	public static Reward valueOf(){
		return new Reward();
	}

	public void addRewardItem(RewardItem item){
		rewardItems.add(item);
	}
}
