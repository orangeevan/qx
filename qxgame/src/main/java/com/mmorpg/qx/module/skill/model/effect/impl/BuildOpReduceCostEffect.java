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
 * @description: 操作建筑免去cd期间多余费用
 * @since 19:15 2020-10-28
 */
public class BuildOpReduceCostEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> creature.getEffectController().setStatus(EffectStatus.Build_Op_Reduce_Cost, false));
            return true;
        }
        //effect.getEffected().getEffectController().setStatus(EffectStatus.Build_Op_Reduce_Cost, false);
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(target -> {
                if (!target.getEffectController().isInStatus(EffectStatus.Build_Op_Reduce_Cost)) {
                    return;
                }
                target.getEffectController().unsetStatus(EffectStatus.Build_Op_Reduce_Cost, false);
            });
        }
    }
}
