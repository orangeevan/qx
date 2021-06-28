package com.mmorpg.qx.module.purse.reward;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.purse.model.PurseType;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardProvider;
import com.mmorpg.qx.module.reward.model.RewardType;
import org.springframework.stereotype.Component;

/**
 * 钱包奖励实现类
 * @author wang ke
 * @since v1.0 2018年3月24日
 *
 */
@Component
public class PurseRewardProvider extends RewardProvider {

	@Override
	public RewardType getType() {
		return RewardType.PURSE;
	}

	@Override
	public void withdraw(Player player, RewardItem rewardItem, ModuleInfo module) {
		PurseType type = PurseType.typeOf(rewardItem.getCode());
		player.getPurse().add(type, rewardItem.getAmount());
	 	// 发送钱包更新消息
		PacketSendUtility.sendPacket(player, player.getPurse().update(Integer.parseInt(rewardItem.getCode())));
	}
}
