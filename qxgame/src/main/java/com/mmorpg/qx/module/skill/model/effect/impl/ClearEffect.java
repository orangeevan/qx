package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 清除某类buff
 * @since 19:04 2020-10-27
 */
public class ClearEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        int type = effect.getEffectResource().getValue();
        if (type > 0) {
            chooseTargets(effect);
            Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
            if (CollectionUtils.isEmpty(creatures)) {
                return false;
            }
            creatures.stream().forEach(creature -> {
                List<Effect> removeEffects = creature.getEffectController().getEffectsByBuffType(type);
                if (!CollectionUtils.isEmpty(removeEffects)) {
                    removeEffects.stream().forEach(e -> creature.getEffectController().removeEffect(e.getEffectResourceId()));
                }
            });
        }
        return true;
    }
}
