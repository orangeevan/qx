package com.mmorpg.qx.module.purse.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.purse.model.Purse;

@Component
public class PurseManager implements EntityOfPlayerUpdateRule {

	@Autowired
	private PlayerManager playerManager;

	@Override
	public void initPlayer(Player player) {
		player.setPurse(Purse.valueOf(player.getPlayerEnt()));
	}

	@Override
	public void update(Player player) {
		playerManager.update(player);

	}

	@Override
	public void logoutWriteBack(Player player) {
		playerManager.logoutWriteBack(player);
	}

}
