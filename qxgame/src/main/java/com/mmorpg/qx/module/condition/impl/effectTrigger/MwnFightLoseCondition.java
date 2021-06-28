package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;

/**
 * @author wang ke
 * @description: 魔物娘之间战斗获胜
 * @since 16:57 2020-11-05
 */
public class MwnFightLoseCondition extends AbstractEffectTriggerCondition<Integer> {

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MWN_FIGHT_LOSE;
    }
}
