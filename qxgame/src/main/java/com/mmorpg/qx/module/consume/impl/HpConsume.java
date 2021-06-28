package com.mmorpg.qx.module.consume.impl;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.AbstractConsume;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;

/**
 * 血量消耗
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class HpConsume extends AbstractConsume<AbstractCreature> {

    @Override
    public void consume(AbstractCreature object, ModuleInfo moduleInfo, int amount) {
        AbstractCreature creature =  object;
        creature.getLifeStats().reduceHp(value * amount, null, Reason.Consume, true);
    }

    @Override
    protected Result doVerify(AbstractCreature object, int amount) {
        AbstractCreature creature =  object;
        if (creature.getLifeStats().getCurrentHp() < value * amount) {
            return Result.valueOf(ManagedErrorCode.NOT_ENOUGH_HP);
        }
        return Result.SUCCESS;
    }

    @Override
    public AbstractConsume clone() {
        HpConsume consume = new HpConsume();
        consume.setCode(code);
        consume.setValue(value);
        return consume;
    }
}
