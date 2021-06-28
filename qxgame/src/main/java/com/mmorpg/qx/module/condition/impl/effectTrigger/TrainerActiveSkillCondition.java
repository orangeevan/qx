package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.resource.SkillResource;

/**
 * @author wang ke
 * @description:驯养师主动魔法
 * @since 14:52 2020-11-03
 */
public class TrainerActiveSkillCondition extends AbstractEffectTriggerCondition<Integer> {

    private SkillReleaseType type;

    @Override
    protected void init() {
        super.init();
        if (getValue() > 0) {
            type = SkillReleaseType.valueOf(getValue());
        }
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        if (super.verify(trigger, triggerType, amount).isSuccess()) {
            if (type != null) {
                SkillResource skillResource = SkillManager.getInstance().getSkillResource(amount);
                if (skillResource.getReleaseType() != type) {
                    return Result.FAILURE;
                }
            }
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.Trainer_Active_SKill;
    }
}
