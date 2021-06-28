package com.mmorpg.qx.module.shop.resource;

import java.util.List;

import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.consume.resource.ConsumeResource;
import com.mmorpg.qx.module.reward.resource.RewardResource;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

@Resource
public class ShopResource {
	@Id
	private int id;
	/** 购买条件 */
	private List<ConditionResource> conditionResources;
	/** 购买消耗 */
	private List<ConsumeResource> consumeResources;
	/** 购买奖励 */
	private List<RewardResource> rewardResources;
	/** 快速购买*/
	private List<RewardResource> quickRewardResources;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ConditionResource> getConditionResources() {
		return conditionResources;
	}

	public void setConditionResources(List<ConditionResource> conditionResources) {
		this.conditionResources = conditionResources;
	}

	public List<ConsumeResource> getConsumeResources() {
		return consumeResources;
	}

	public void setConsumeResources(List<ConsumeResource> consumeResources) {
		this.consumeResources = consumeResources;
	}

	public List<RewardResource> getRewardResources() {
		return rewardResources;
	}

	public void setRewardResources(List<RewardResource> rewardResources) {
		this.rewardResources = rewardResources;
	}

	public List<RewardResource> getQuickRewardResources() {
		return quickRewardResources;
	}

	public void setQuickRewardResources(List<RewardResource> quickRewardResources) {
		this.quickRewardResources = quickRewardResources;
	}

}
