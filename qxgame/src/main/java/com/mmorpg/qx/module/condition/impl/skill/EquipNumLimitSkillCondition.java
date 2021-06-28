package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description:装备数量达到多少件不能释放
 * @since 15:11 2020-11-20
 */
public class EquipNumLimitSkillCondition extends AbstractSkillCondition {

    @Override
    public Result verify(Skill skill, Effect effect, Integer amount) {
        AbstractCreature skillCaster = skill.getSkillCaster();
        AbstractTrainerCreature master = skillCaster.getMaster();
        if (master.getEquipmentStorage().getEquips().size() < getValue()) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
