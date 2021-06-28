package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.event.KillMonsterEvent;
import com.mmorpg.qx.module.object.gameobject.*;
import com.haipaite.common.event.core.EventBusManager;

public class MonsterController extends NpcController {

    private boolean respwan = true;

    @Override
    public MonsterCreature getOwner() {
        return (MonsterCreature) super.getOwner();
    }

    @Override
    public void onDie(AbstractCreature lastAttacker, int skillId, int effectId) {
        super.onDie(lastAttacker, skillId, effectId);
        MonsterCreature owner = getOwner();

        // 这里可能要用来处理掉落和经验
        doReward(lastAttacker, owner.getAggroList());

        // owner.getAi().handleEvent(Event.DIED);
        owner.getController().onFightOff();
        // deselect target at the end
        owner.setTarget(null);
        // 调用重生计划任务
        scheduleRelive();
        AbstractTrainerCreature killer = lastAttacker.getMaster();
        EventBusManager.getInstance().submit(KillMonsterEvent.valueOf(killer, getOwner().getObjectKey()));
    }

    private void scheduleRelive() {
    }

    public boolean isRespwan() {
        return respwan;
    }

    public void setRespwan(boolean respwan) {
        this.respwan = respwan;
    }

}
