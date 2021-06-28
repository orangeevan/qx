package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
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
 * @description: 改变魔物娘武力值
 * @since 20:42 2020-10-27
 */
public class AlterMwnTrainerHarmEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effected = effect.getEffected();
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effected.getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                if (RelationshipUtils.isMWN(creature)) {
                    creature.getEffectController().setStatus(EffectStatus.Alter_Mwn_Trainer_Harm, false);
                }
            });
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
                if (!creature.getEffectController().isInStatus(EffectStatus.Alter_Mwn_Trainer_Harm)) {
                    return;
                }
                creature.getEffectController().unsetStatus(EffectStatus.Alter_Mwn_Trainer_Harm, false);
            });
        }
    }
}
