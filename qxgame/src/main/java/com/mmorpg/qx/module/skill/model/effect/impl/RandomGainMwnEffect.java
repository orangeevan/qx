package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author wang ke
 * @description:随机获取一张或数张卡牌
 * @since 20:24 2020-11-03
 */
public class RandomGainMwnEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effected = effect.getTrigger();
        if (Objects.isNull(effected)) {
            chooseTargets(effect);
            Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
            if (CollectionUtils.isEmpty(creatures)) {
                return false;
            }
            effected = creatures.iterator().next();
        }
        AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(effected);
        if (Objects.isNull(trainer)) {
            return false;
        }
        int num = effect.getValue();
        //加牌过程走回合获取卡牌过程，不足掉血
        RoundFightService.getInstance().roundAddCard(trainer, num, Reason.Trainer_Skill_Effect);
        return true;
    }
}
