package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author chy.yuen
 * @description: 连袭
 * @since 18:00 2020/12/23
 */
public class LianXiCondition extends AbstractEffectTriggerCondition<Integer> {


    @Override
    public TriggerType getTriggerType() {
        return TriggerType.LIANX_XI;
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        return super.verify(trigger, triggerType, amount);
    }
}
