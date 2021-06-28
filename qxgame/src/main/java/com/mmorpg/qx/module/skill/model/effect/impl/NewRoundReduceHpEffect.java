package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.Reason;
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
 * @description: 新回合初扣除血量
 * @since 16:54 2020-10-28
 */
public class NewRoundReduceHpEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            int reduce = 0;
            if (effect.getEffectResource().isValueRate()) {
                reduce = Math.round(effect.getEffected().getLifeStats().getCurrentHp() * GameUtil.toRatio10000(effect.getValue()));
                effect.setValue(reduce);
            } else {
                reduce = effect.getValue();
            }
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature trainer = creature.getMaster();
                if (trainer.isAlive() && trainer.getRoom().getCurrentTurn() == trainer && trainer.getRoom().getRoundStage() == RoundStage.Extract_Card_Before) {
                    trainer.getLifeStats().reduceMp(reduce, Reason.Mwn_Skill_Effect, false, false);
                    return true;
                }
            }
        }
        return false;
    }
}
