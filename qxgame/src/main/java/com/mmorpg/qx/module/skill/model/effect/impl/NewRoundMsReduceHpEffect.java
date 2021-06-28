package com.mmorpg.qx.module.skill.model.effect.impl;

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
 * @description: 新回合初扣除受魔法强度影响血量
 * @since 16:54 2020-10-28
 */
public class NewRoundMsReduceHpEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature trainer = creature.getMaster();
                if (trainer.isAlive() && trainer.getRoom().getCurrentTurn() == trainer && trainer.getRoom().getRoundStage() == RoundStage.Extract_Card_Before) {
                    effect.setValue((int) (trainer.getMagicStrength() * 200));
                    trainer.getLifeStats().reduceMp(effect.getValue(), Reason.Mwn_Skill_Effect, false, false);
                    return true;
                }
            }
        }
        return false;
    }
}
