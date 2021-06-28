package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 增减生命值
 * @since 16:56 2020-09-04
 */
public class HpAlterEffect extends AbstractEffectTemplate {
    private static final Logger logger = SysLoggerFactory.getLogger(HpAlterEffect.class);
    private int value;

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
        value = effect.getValue();
    }

    @Override
    public boolean verifyStrengthCond(Conditions condition, Effect effect) {
        Result verify = condition.verify(effect.getEffected(), effect.getEffector(), 0);
        if (verify.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                if (value > 0) {
                    creature.getLifeStats().increaseHp(value, Reason.Mwn_Skill_Effect, false, false);
                } else {
                    creature.getLifeStats().reduceHp(-value, effect.getEffected(), effect.getSkillResourceId(), effect.getEffectResource().getId());
                }
                String hpAlter = String.format("对象 【%s】 受HpAlterEffect改变 生命值变化 【%s】", creature.getObjectId(), value);
                logger.info(hpAlter);
            });
        }
        return true;
    }
}
