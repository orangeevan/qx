package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.common.enums.TriggerType;

/**
 * @author wang ke
 * @description: 死亡动作触发
 * @since 17:48 2020-09-24
 */
public class AfterDieCondition extends AbstractEffectTriggerCondition {

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.DIE;
    }

}
