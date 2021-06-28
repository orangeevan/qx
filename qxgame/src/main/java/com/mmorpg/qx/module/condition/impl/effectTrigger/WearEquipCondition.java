package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * @author wang ke
 * @description: 穿戴装备触发
 * @since 16:39 2020-11-20
 */
public class WearEquipCondition extends AbstractEffectTriggerCondition<Integer> {


    @Override
    public TriggerType getTriggerType() {
        return TriggerType.WEAR_EQUIP;
    }


    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        if (super.verify(trigger, triggerType, amount).isSuccess()) {
            if (amount == getValue()) {
                return Result.SUCCESS;
            }
        }
        return Result.FAILURE;
    }
}
