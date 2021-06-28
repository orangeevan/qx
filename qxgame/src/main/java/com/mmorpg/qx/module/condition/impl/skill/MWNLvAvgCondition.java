package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.Collection;
import java.util.Objects;

/**
 * @author wang ke
 * @description: 魔物娘平均等级条件
 * @since 20:48 2020-09-09
 */
public class MWNLvAvgCondition extends AbstractSkillCondition {
    private int avgLv;

    @Override
    protected void init() {
        super.init();
        avgLv = Integer.valueOf(getParams());
    }

    @Override
    public Result verify(Skill skill, Effect effect, Integer amount) {
        AbstractTrainerCreature trainer = (AbstractTrainerCreature) effect.getEffected();
        Collection<MWNCreature> mwnCreatures = trainer.getMWN(true);
        if (Objects.isNull(mwnCreatures)) {
            return Result.FAILURE;
        }
        int lvAvg = (int) mwnCreatures.stream().mapToInt(MWNCreature::getLevel).average().getAsDouble();
        if (lvAvg >= avgLv) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
