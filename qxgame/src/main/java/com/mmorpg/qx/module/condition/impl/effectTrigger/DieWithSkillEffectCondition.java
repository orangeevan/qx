package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;

/**
 * @author wang ke
 * @description:技能效果死亡触发
 * @since 14:53 2020-11-03
 */
public class DieWithSkillEffectCondition extends AbstractEffectTriggerCondition {
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.DIE_BY_SKILL_EFFECT;
    }
}
