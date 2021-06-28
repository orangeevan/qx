package com.mmorpg.qx.module.player.model.loginposition.impl;

import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.player.model.LoginPositionType;
import com.mmorpg.qx.module.player.model.loginposition.AbstractLoginPositionHandle;

/**
 * 默认登陆处理器,新角色第一次进入游戏
 * @author wang ke
 * @since v1.0 2018年3月8日
 *
 */
@Component
public class DefaultLoginPositionHandle extends AbstractLoginPositionHandle {

	@Override
	public LoginPositionType getType() {
		return LoginPositionType.DEFAULT;
	}

	@Override
	public void loginPosition(PlayerTrainerCreature player) {
//		// TODO 设置到出生点
//		int mapId = 1002;
//		int x = 10;
//		int y = 10;
//		WorldMap worldMap = World.getInstance().getWorldMap(mapId);
//		WorldMapInstance minInstance = null;
//
//		for (WorldMapInstance instance : worldMap.getInstances().values()) {
//			if (minInstance == null || minInstance.getPlayerCount() > instance.getPlayerCount()) {
//				minInstance = instance;
//			}
//		}
//		if (player.getPosition() == null) {
//			player.setPosition(new WorldPosition());
//		}
//		World.getInstance().setPosition(player, minInstance, x, y, (byte) 1);
	}

}
