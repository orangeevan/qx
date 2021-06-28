package com.mmorpg.qx.module.object.controllers.stats;

import com.mmorpg.qx.module.object.gameobject.BossCreature;

public class BossLifeStats extends CreatureLifeStats<BossCreature> {

	public BossLifeStats(BossCreature owner, int currentHp, int currentMp, boolean die) {
		super(owner, currentHp, currentMp);
	}


	@Override
	protected void onIncreaseMp(long value) {
	}

	@Override
	protected void onReduceMp() {
	}

	@Override
	protected void onIncreaseHp(long value) {
	}

}
