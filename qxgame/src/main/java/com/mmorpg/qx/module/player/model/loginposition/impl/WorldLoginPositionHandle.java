package com.mmorpg.qx.module.player.model.loginposition.impl;

import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.player.model.LoginPosition;
import com.mmorpg.qx.module.player.model.LoginPositionType;
import com.mmorpg.qx.module.player.model.loginposition.AbstractLoginPositionHandle;

/**
 * 世界场景登录器
 * @author wang ke
 * @since v1.0 2018年3月8日
 *
 */
@Component
public class WorldLoginPositionHandle extends AbstractLoginPositionHandle {

	@Override
	public LoginPositionType getType() {
		return LoginPositionType.WORLD_SCENE;
	}

	@Override
	public void loginPosition(PlayerTrainerCreature player) {

//		WorldMap worldMap = World.getInstance().getWorldMap(player.getLoginPosition().getMapId());
//		WorldMapInstance selectInstance = null;
//		// 先选择玩家先前的
//		for (WorldMapInstance instance : worldMap.getInstances().values()) {
//			if (player.getLoginPosition().getInstanceId() == instance.getInstanceId()) {
//				selectInstance = instance;
//			}
//		}
//		// 如果该线已经被回收了,那么选择一条人数未满的线
//		if (selectInstance == null) {
//			for (WorldMapInstance instance : worldMap.getInstances().values()) {
//				if (instance.getPlayerCount() < worldMap.getResource().getMaxNum()) {
//					selectInstance = instance;
//				}
//			}
//		}
//
//		if (player.getPosition() == null) {
//			player.setPosition(new WorldPosition());
//		}
//		World.getInstance().setPosition(player, selectInstance, player.getLoginPosition().getX(),
//				player.getLoginPosition().getY(), (byte) 1);
	}

	@Override
	public void onMove(PlayerTrainerCreature player) {
		player.getLoginPosition().setGridId(player.getGridId());
	}

	@Override
	public void onSpaw(PlayerTrainerCreature player) {
		if (player.getLoginPosition() == null) {
			player.setLoginPosition(new LoginPosition(LoginPositionType.WORLD_SCENE));
		}
		if (player.getLoginPosition().getType() != LoginPositionType.WORLD_SCENE) {
			player.getLoginPosition().setType(LoginPositionType.WORLD_SCENE);
		}
		onMove(player);
	}

}
