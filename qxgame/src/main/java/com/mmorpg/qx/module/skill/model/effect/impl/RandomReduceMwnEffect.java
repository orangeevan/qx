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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author wang ke
 * @description:随机丢失一张或数张卡牌
 * @since 20:29 2020-11-03
 */
public class RandomReduceMwnEffect extends AbstractEffectTemplate {


    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                if (!RelationshipUtils.isTrainer(creature)) {
                    return false;
                }
                AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(creature);
                if (Objects.isNull(trainer)) {
                    return false;
                }
                int num = effect.getValue();
                //丢牌过程
                List<MoWuNiang> lossCards = new ArrayList<>();
                if (trainer.getUseCardStorage().getCurrentSize() < num) {
                    num = trainer.getUseCardStorage().getCurrentSize();
                    trainer.getUseCardStorage().getMwns().stream().forEach(mwn -> {
                        trainer.getUseCardStorage().reduceMwn(mwn.getResourceId());
                        lossCards.add(mwn);
                    });
                } else {
                    lossCards.addAll(RoundFightService.getInstance().randomCard(trainer.getUseCardStorage(), num));
                }
                RoundFightUtils.trainerCardsChange(trainer, lossCards, false, false, Reason.Trainer_Skill_Effect);
            }
            return true;
        }
        return false;
    }
}
