package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 溅射伤害
 * @since 16:22 2020-09-07
 */
public class SplashDamageEffect extends AbstractEffectTemplate {

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature effector : creatures) {
                //施法者添加溅射状态
                effector.getEffectController().setStatus(EffectStatus.Splash, false);
            }
        }
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> creatures = getTargets();
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                creature.getEffectController().unsetStatus(EffectStatus.Splash, false);
            });
        }
    }
}
