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
 * @description: 金币获取数量提升
 * @since 20:13 2020-10-28
 */
public class IncGoldEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                creature.getEffectController().setStatus(EffectStatus.Inc_Gold, false);
            });
            return true;
        }
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(creature -> {
                creature.getEffectController().unsetStatus(EffectStatus.Inc_Gold, false);
            });
        }
    }
}
