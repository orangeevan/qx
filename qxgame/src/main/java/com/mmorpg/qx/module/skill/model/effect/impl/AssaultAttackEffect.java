package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description:强袭
 * @since 16:09 2020-10-23
 */
public class AssaultAttackEffect extends AbstractEffectTemplate {

    private int harm;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        harm = resource.getValue();
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (CollectionUtils.isEmpty(creatures)) {
            return false;
        }
        creatures.stream().forEach(creature -> {
            AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(creature);
            int realHarm = harm > trainerCreature.getCurrentHp() ? trainerCreature.getCurrentHp() : harm;
            trainerCreature.getLifeStats().reduceHp(realHarm, effect.getEffected(), Reason.Mwn_Skill_Effect, false);
        });
        effect.getEffector().getEffectController().setStatus(EffectStatus.Mwn_Assault, false);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        AbstractCreature effector = effect.getEffector();
        if (!effect.getEffected().getEffectController().isInStatus(EffectStatus.Mwn_Assault)) {
            return;
        }
        effector.getEffectController().unsetStatus(EffectStatus.Mwn_Assault, false);
    }
}
