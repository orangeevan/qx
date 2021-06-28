package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description: 施法者生命值百分比
 * @since 21:09 2020-09-09
 */
public class EffectorHpPercentCondition extends AbstractSkillCondition {
    private float percent;

    @Override
    protected void init() {
        super.init();
        percent = Float.valueOf(getParams());
    }

    @Override
    public Result verify(Skill param1, Effect effect, Integer amount) {
        AbstractCreature creature = effect.getEffector();
        float hpPer = creature.getLifeStats().getCurrentHp() * 1.0f / creature.getLifeStats().getMaxHp();
        if (hpPer >= percent) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
