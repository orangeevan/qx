package com.mmorpg.qx.module.condition.impl.skill;

import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author wang ke
 * @description:魔物娘数量
 * @since 21:20 2020-09-09
 */
public class MWNNumsCondition extends AbstractSkillCondition {
    private int num;

    @Override
    protected void init() {
        super.init();
        num = getValue();
    }

    @Override
    public Result verify(Skill skill, Effect effect, Integer amount) {
        AbstractTrainerCreature trainer = effect.getEffected().getMaster();
        Collection<MWNCreature> mwn = trainer.getMWN(true);
        if (CollectionUtils.isEmpty(mwn) || mwn.size() < num) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
