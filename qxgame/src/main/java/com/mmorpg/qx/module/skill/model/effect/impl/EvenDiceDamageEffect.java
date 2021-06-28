package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 投骰子点数为偶数造成伤害
 * @since 11:47 2020-10-26
 */
public class EvenDiceDamageEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effector = effect.getEffector();
        AbstractTrainerCreature master = effector.getMaster();
        if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master || !master.getRoom().isInStage(RoundStage.Throw_Dice)) {
            return false;
        }
        if (master.getDicePoint().calcPoints() % 2 != 0) {
            return false;
        }
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature ->
                    creature.getLifeStats().reduceHp(effect.getValue(), effector, 0, effect.getEffectResourceId())
            );
            return true;
        }
        return false;
    }
}
