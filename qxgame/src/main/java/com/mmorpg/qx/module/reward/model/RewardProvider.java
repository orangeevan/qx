package com.mmorpg.qx.module.reward.model;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.manager.RewardManager;

/**
 * 奖励发放器
 * 
 * @author wang ke
 * @since v1.0 2013-1-24
 * 
 */
public abstract class RewardProvider {

	@Autowired
	private RewardManager rewardManager;

	@PostConstruct
	public void init() {
		rewardManager.registerProvider(this);
	}

	public abstract RewardType getType();

	/**
	 * 获取奖励内容
	 * 
	 * @param
	 * @return
	 */
	public abstract void withdraw(Player player, RewardItem rewardItem, ModuleInfo module);

}
