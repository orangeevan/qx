package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;

/**
 * @author wang ke
 * @description:魔物娘之间战斗死亡触发
 * @since 14:54 2020-11-03
 */
public class DieWithMwnFightCondition extends AbstractEffectTriggerCondition {
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.DIE_MWN_MWN;
    }
}
