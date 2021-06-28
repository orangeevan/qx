package com.mmorpg.qx.module.player.model.loginposition;

import javax.annotation.PostConstruct;

import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.LoginPositionType;

/**
 * 玩家登陆坐标处理器
 * @author wang ke
 * @since v1.0 2019年2月8日
 *
 */
public abstract class AbstractLoginPositionHandle {

	@Autowired
	private PlayerManager playerManager;

	@PostConstruct
	public void init() {
		playerManager.registerProvider(this);
	}

	public abstract LoginPositionType getType();

	/**
	 * 登陆处理
	 * @param player
	 */
	public abstract void loginPosition(PlayerTrainerCreature player);

	/**
	 * 移动时处理器
	 * @param player
	 */
	public void onMove(PlayerTrainerCreature player) {

	}

	/**
	 * 出生时处理器
	 * @param player
	 */
	public void onSpaw(PlayerTrainerCreature player) {

	}

}
