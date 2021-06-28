package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description: buff生效后触发其他buff
 * @since 11:58 2020-10-23
 */
public class EffectApplyTriggerCondition extends AbstractEffectTriggerCondition<Effect> {

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.Effect_Trigger;
    }

    private int dependEffect;

    @Override
    protected void init() {
        dependEffect = getValue();
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Effect effect) {

        if (super.verify(trigger, triggerType, effect).isFailure()) {
            return Result.FAILURE;
        }
        if (effect.getEffectResourceId() != dependEffect) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
