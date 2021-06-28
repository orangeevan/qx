package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.packet.RoundCardUpdateResp;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author wang ke
 * @description: 随机从牌库丢失一张或多张卡牌
 * @since 20:36 2020-11-03
 */
public class RandomReduceMwnSourceEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effected = effect.getTrigger();
        if (Objects.isNull(effected)) {
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
        //丢牌过程
        List<MoWuNiang> lossCards = new ArrayList<>();
        if (trainer.getSourceCardStorage().getCurrentSize() < num) {
            num = trainer.getUseCardStorage().getCurrentSize();
        }
        lossCards.addAll(RoundFightService.getInstance().randomCard(trainer.getSourceCardStorage(), num));
        PacketSendUtility.sendPacket(trainer, RoundCardUpdateResp.valueOf(trainer.getObjectId(), lossCards, false, true, Reason.Trainer_Skill_Effect));
        return true;
    }
}
