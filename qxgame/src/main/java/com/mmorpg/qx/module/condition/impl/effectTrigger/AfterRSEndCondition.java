package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;

/**
 * @author wang ke
 * @description: 回合结束阶段触发条件
 * @since 16:45 2020/12/19
 */
public class AfterRSEndCondition extends AbstractEffectTriggerCondition {
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.RoundStage_End;
    }

}
