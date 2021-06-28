package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description:死亡后随机失去卡牌抽取卡牌
 * @since 13:50 2020-09-27
 */
public class GainLossCardAfterDieEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effected = effect.getTrigger();
        AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(effected);
        if (Objects.isNull(trainer)) {
            return false;
        }
        int num = effect.getValue();
        //加牌过程走回合获取卡牌过程，不足掉血
        RoundFightService.getInstance().roundAddCard(trainer, num, Reason.Round_Extract_Card);
        //丢牌过程
        List<MoWuNiang> lossCards = new ArrayList<>();
        if (trainer.getUseCardStorage().getCurrentSize() < num) {
            num = trainer.getUseCardStorage().getCurrentSize();
            trainer.getUseCardStorage().getMwns().forEach(mwn -> {
                trainer.getUseCardStorage().reduceMwn(mwn.getResourceId());
                lossCards.add(mwn);
            });
        } else {
            lossCards.addAll(RoundFightService.getInstance().randomCard(trainer.getUseCardStorage(), num));
        }
        RoundFightUtils.trainerCardsChange(trainer, lossCards, false, false, Reason.Mwn_Skill_Effect);
        return true;
    }
}
