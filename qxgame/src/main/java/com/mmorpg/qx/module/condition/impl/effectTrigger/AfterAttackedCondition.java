package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.common.enums.TriggerType;

/**
 * @author wang ke
 * @description:受攻击后触发
 * @since 17:33 2020-09-24
 */
public class AfterAttackedCondition extends AbstractEffectTriggerCondition {
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ATTACKED;
    }
}
