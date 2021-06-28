package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description:被施法者生命值达到具体数值
 * @since 21:14 2020-09-09
 */
public class SkillEffectedHpCondition extends AbstractSkillCondition {

    private int hp;

    @Override
    protected void init() {
        super.init();
        hp = Integer.valueOf(getParams());
    }

    @Override
    public Result verify(Skill param1, Effect effect, Integer amount) {
        AbstractCreature creature = effect.getEffected();
        int obHp = creature.getLifeStats().getCurrentHp();
        if (obHp >= hp) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
