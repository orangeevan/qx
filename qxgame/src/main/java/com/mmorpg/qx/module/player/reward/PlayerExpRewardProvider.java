package com.mmorpg.qx.module.player.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.player.event.PlayerLevelUpEvent;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.packet.PlayerExpAddResp;
import com.mmorpg.qx.module.player.resource.PlayerLevelResource;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardProvider;
import com.mmorpg.qx.module.reward.model.RewardType;
import com.haipaite.common.event.core.EventBusManager;

/**
 * 玩家经验获取
 * @author wang ke
 * @since v1.0 2018年4月8日
 *
 */
@Component
public class PlayerExpRewardProvider extends RewardProvider {

	@Autowired
	private PlayerManager playerManager;

	@Override
	public RewardType getType() {
		return RewardType.EXP;
	}

	@Override
	public void withdraw(Player player, RewardItem rewardItem, ModuleInfo module) {
		player.getPlayerEnt().setExp(player.getPlayerEnt().getExp() + rewardItem.getAmount());
		PlayerLevelResource resource = playerManager.getPlayerLeveltResources(player.getPlayerEnt().getLevel());
		int oldLevel = player.getPlayerEnt().getLevel();
		boolean uplevel = false;
		while (player.getPlayerEnt().getExp() >= resource.getExp()) {
			// 升级
			player.getPlayerEnt().setLevel(oldLevel + 1);
			player.getPlayerEnt().setExp(player.getPlayerEnt().getExp() - resource.getExp());
			resource = playerManager.getPlayerLeveltResources(player.getPlayerEnt().getLevel());
			uplevel = true;
		}

		if (uplevel) {
			EventBusManager.getInstance().submit(PlayerLevelUpEvent.valueOf(player, oldLevel));
		}
		PacketSendUtility.sendPacket(player, PlayerExpAddResp.valueOf(rewardItem.getAmount(),
				player.getPlayerEnt().getExp(), player.getPlayerEnt().getLevel()));

	}

}
