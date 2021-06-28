package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.BossController;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

/**
 * BOSS
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
public class BossCreature extends MonsterCreature {

	// private BossScheme bossScheme;

	public BossCreature(long objId, BossController controller, WorldPosition position) {
		super(objId, controller, position);
	}

	@Override
	public BossController getController() {
		return (BossController) super.getController();
	}

//	@Override
//	public boolean isAtSpawnLocation() {
//		if (getX() == getBornX() && getY() == getBornY()) {
//			return true;
//		}
//		return false;
//	}

	@Override
	public ObjectType getObjectType() {
		return ObjectType.BOSS;
	}

	/*
	public BossScheme getBossScheme() {
		return bossScheme;
	}
	
	public void setBossScheme(BossScheme bossScheme) {
		this.bossScheme = bossScheme;
	}
	
	@Override
	public void onDrop(Reward reward, Creature lastAttacker, Player mostDamagePlayer) {
		super.onDrop(reward, lastAttacker, mostDamagePlayer);
		if (bossScheme != null) {
			if (lastAttacker instanceof Summon) {
				bossScheme.getBossHistory().addDropInfo(((Summon) lastAttacker).getMaster(), reward, this);
			} else if (lastAttacker instanceof Player) {
				bossScheme.getBossHistory().addDropInfo((Player) lastAttacker, reward, this);
			}
		}
	}
	*/

}
