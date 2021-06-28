package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.GoldOreSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;

import java.util.List;

/**
 * @author wang ke
 * @description: 投骰子前可用，释放后，本回合内移动途径敌方驯养师时获得敌方身上一半金币。
 * @since 14:23 2020-10-26
 */
public class TrainerStealGoldSkillProcessor extends AbstractSkillProcessor<GoldOreSkillResult, Integer> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Trainer_Steal_Gold;
    }

    @Override
    public List process(Skill skill, int roundIndex) {
        AbstractTrainerCreature master = skill.getSkillCaster().getMaster();
        if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master || !master.getRoom().isInStage(RoundStage.Throw_Dice_Before)) {
            return null;
        }
        master.getEffectController().setStatus(EffectStatus.Steal_Gold,false);
        return null;
    }

    @Override
    public Integer initParam(SkillResource resource) {
        return null;
    }
}
