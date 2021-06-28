package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author wang ke
 * @description: 偷取对方驯养师金币
 * @since 16:09 2020-10-27
 */
public class StealGoldEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature trigger = effect.getTrigger();
        if (Objects.isNull(trigger)) {
            chooseTargets(effect);
            Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
            if (CollectionUtils.isEmpty(creatures)) {
                return false;
            }
            trigger = creatures.iterator().next();
        }
        if (!RelationshipUtils.isTrainer(trigger)) {
            return false;
        }
        int value = effect.getValue();
        trigger.getLifeStats().reduceGold(value, Reason.Mwn_Skill_Effect, false);
        effect.getEffected().getMaster().getLifeStats().increaseGold(value, Reason.Mwn_Skill_Effect, true);
        return true;
    }
}
