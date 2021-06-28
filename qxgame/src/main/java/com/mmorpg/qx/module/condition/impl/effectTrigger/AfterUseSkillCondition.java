package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;

/**
 * @author wang ke
 * @description:使用技能后触发
 * @since 14:56 2020-11-03
 */
public class AfterUseSkillCondition extends AbstractEffectTriggerCondition {

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.SKILL_USE;
    }
}
