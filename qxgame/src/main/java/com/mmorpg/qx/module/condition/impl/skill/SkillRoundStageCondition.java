package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 技能处于某个阶段使用
 * @since 19:18 2020-10-22
 */
public class SkillRoundStageCondition extends AbstractSkillCondition {

    private RoundStage roundStage;

    @Override
    protected void init() {
        String params = getParams();
        Optional<RoundStage> any = Arrays.stream(RoundStage.values()).filter(rs -> rs.name().equalsIgnoreCase(params)).findAny();
        if (any.isPresent()) {
            roundStage = any.get();
        } else {
            throw new IllegalArgumentException("SkillRoundStageCondition params error");
        }
    }

    @Override
    public Result verify(Skill param1, Effect param2, Integer amount) {
        AbstractTrainerCreature master = param1.getSkillCaster().getMaster();
        boolean result = master.getRoom().isInStage(roundStage);
        if (result) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
