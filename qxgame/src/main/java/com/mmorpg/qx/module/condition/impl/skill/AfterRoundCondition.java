package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description: 某个回合以后
 * @since 20:34 2020-09-10
 */
public class AfterRoundCondition extends AbstractSkillCondition {
    private int beginRound;

    @Override
    protected void init() {
        super.init();
        beginRound = getValue();
    }

    @Override
    public Result verify(Skill skill, Effect param2, Integer amount) {
        int round = skill.getSkillCaster().getMaster().getRoom().getRound();
        if (round >= beginRound) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
