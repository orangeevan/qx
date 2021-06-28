package com.mmorpg.qx.module.object.controllers.event;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.haipaite.common.event.event.IEvent;

/**
 * 杀怪事件
 * @author wang ke
 * @since v1.0 2018年4月3日
 *
 */
public class KillMonsterEvent implements IEvent {
	private AbstractTrainerCreature trainer;
	private int objectKey;

	public static KillMonsterEvent valueOf(AbstractTrainerCreature player, int objectKey) {
		KillMonsterEvent resp = new KillMonsterEvent();
		resp.trainer = player;
		resp.objectKey = objectKey;
		return resp;
	}

	public AbstractTrainerCreature getTrainer() {
		return trainer;
	}

	public void setTrainer(PlayerTrainerCreature trainer) {
		this.trainer = trainer;
	}

	public int getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(int objectKey) {
		this.objectKey = objectKey;
	}

	@Override
	public long getOwner() {
		return trainer.getObjectId();
	}

}
