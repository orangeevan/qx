package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 护盾效果, 有使用次数跟持续时间限制
 * @since 16:51 2020-09-04
 */
public class ShieldEffect extends AbstractEffectTemplate {
    //吸收伤害，达到最大值护盾消失
    private int absorbDamage;

    //最大吸收伤害
    private long maxAbsorbDamage;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
    }

    @Override
    public void calculate(Effect effect) {
        maxAbsorbDamage = effect.getValue() * effect.getEffector().getLifeStats().getMaxHp();
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature effector : creatures) {
                effector.getEffectController().setStatus(EffectStatus.Shield, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> creatures = getTargets();
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                creature.getEffectController().unsetStatus(EffectStatus.Shield, true);
            });
        }
    }

    public void absorbDamage(int damage) {
        absorbDamage += damage;
    }

    public boolean isAbsorbFull() {
        return absorbDamage >= maxAbsorbDamage;
    }

    public int leftAbsorbDamge() {
        return (int) (maxAbsorbDamage - absorbDamage);
    }
}
