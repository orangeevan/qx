package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 17:00 2021/4/20
 */
public class ConsumeMpRoundCondition extends AbstractEffectTriggerCondition {
    private int count;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CONSUME_MP_ROUND;
    }

    @Override
    public void init(ConditionResource resource) {
        super.init();
        count = getValue();
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object obj) {

        if (super.verify(trigger, triggerType, obj).isFailure()) {
            return Result.FAILURE;
        }
        return count <= trigger.getConsumeMpRS() ? Result.SUCCESS : Result.FAILURE;
    }

}

