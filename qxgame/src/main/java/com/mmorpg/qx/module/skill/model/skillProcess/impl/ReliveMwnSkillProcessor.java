package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wang ke
 * @description: 复活魔物娘
 * @since 21:17 2020-08-31
 */
@Component
public class ReliveMwnSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, Object> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Relive_Mwn;
    }


    @Override
    public List<DisplaySkillResult> process(Skill skill, int roundIndex) {
        List<DisplaySkillResult> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(skill.getTarget().getTargetIds())) {
            return results;
        }
        AbstractCreature skillCaster = skill.getSkillCaster();
        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(skillCaster);
        List<AbstractCreature> targetList = new ArrayList<>();
        for (long targetId : skill.getTarget().getTargetIds()) {
            targetList.add(trainerCreature.getMwn(targetId));
        }
        if (CollectionUtils.isEmpty(targetList)) {
            return results;
        }
        for (AbstractCreature creature : targetList) {
            if (RelationshipUtils.isMWN(creature)) {
                MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
                if (mwnCreature.isAlreadyDead()) {
                    //复活魔物娘添加卡牌
                    MoWuNiang moWuNiang = mwnCreature.getMwn();
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skillCaster);
                    trainer.removeMwnCreature(mwnCreature.getObjectId());
                    if (trainer.getUseCardStorage().addMwn(moWuNiang)) {
                        RoundFightUtils.trainerCardsChange(trainer, Stream.of(moWuNiang).collect(Collectors.toList()), true, false, Reason.Trainer_Skill_Effect);
                        results.add(DisplaySkillResult.valueOf(moWuNiang.getId(), skill));
                        System.err.println(String.format("驯养师【%s】 复活魔物娘【%s】", trainer.getObjectId(), moWuNiang.getId() + "|" + moWuNiang.getResource().getName()));
                    } else {
                        throw new ManagedException(ManagedErrorCode.USE_STORAGE_FULL);
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
