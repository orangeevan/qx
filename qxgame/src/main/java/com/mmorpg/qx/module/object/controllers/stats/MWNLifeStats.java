package com.mmorpg.qx.module.object.controllers.stats;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

public class MWNLifeStats extends CreatureLifeStats<MWNCreature> {

    public MWNLifeStats(AbstractCreature owner, int currentHp, int currentMp, boolean die) {
        super(owner, currentHp, currentMp);
        this.getIsDead().set(die);
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
