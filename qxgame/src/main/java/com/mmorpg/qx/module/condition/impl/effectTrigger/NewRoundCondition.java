package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.common.enums.TriggerType;

/**
 * @author wang ke
 * @description: 新回合动作触发
 * @since 17:40 2020-09-24
 */
public class NewRoundCondition extends AbstractEffectTriggerCondition {
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ROUND_BEGIN;
    }
}
