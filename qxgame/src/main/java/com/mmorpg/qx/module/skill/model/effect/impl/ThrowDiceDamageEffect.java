package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description:投骰子后根据骰子点数对驯养师造成伤害
 * @since 14:33 2020/12/23
 */
public class ThrowDiceDamageEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractTrainerCreature master = effect.getEffected().getMaster();
        DicePoint dicePoint = master.getDicePoint();
        int value = dicePoint.calcPoints();
        effect.setValue(value);
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                creature.getLifeStats().reduceHp(value, effect.getEffected(), effect.getSkillResourceId(), effect.getEffectResource().getId());
            });
        }
        return true;
    }

}
