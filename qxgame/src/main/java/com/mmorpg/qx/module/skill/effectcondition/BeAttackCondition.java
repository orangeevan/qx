package com.mmorpg.qx.module.skill.effectcondition;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:09 2021/4/23
 */
public class BeAttackCondition extends AbstractCondition<Skill, Effect, Integer> {


    @Override
    protected void init() {
        super.init();

    }

    @Override
    public Result verify(Skill skill, Effect effect, Integer amount) {

        return Result.SUCCESS;
    }

}

