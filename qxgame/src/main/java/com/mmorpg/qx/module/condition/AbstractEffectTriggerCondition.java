package com.mmorpg.qx.module.condition;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.common.enums.TriggerType;

/**
 * @author wang ke
 * @description: 动作类引发buff效果生效
 * @since 18:04 2020-09-24
 */
public abstract class AbstractEffectTriggerCondition<P> extends AbstractCondition<AbstractCreature, TriggerType,P> {
    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, P amount) {
        return triggerType == getTriggerType() ? Result.SUCCESS : Result.FAILURE;
    }

    public abstract TriggerType getTriggerType();
}
