package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 判定胜利方效果
 * @since 11:46 2020-09-07
 */
public class JudgeWinnerEffect extends AbstractEffectTemplate {

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (CollectionUtils.isEmpty(creatures)) {
            return false;
        }
        AbstractCreature creature = creatures.iterator().next();
        AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(creature);
        Room room = trainer.getRoom();
        room.setWinner(trainer);
        room.getRoundFightOver().compareAndSet(false, true);
        effect.setEffectTarget(Target.valueOf(trainer.getGridId(), trainer.getObjectId()));
        return true;
    }
}
