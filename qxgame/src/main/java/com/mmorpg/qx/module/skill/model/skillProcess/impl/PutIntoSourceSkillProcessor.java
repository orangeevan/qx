package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhang peng
 * @description 魔物娘置入牌库
 * @since 16:36 2021/4/14
 */
@Component
public class PutIntoSourceSkillProcessor extends AbstractSkillProcessor {

    @Override
    public SkillType getSkillType() {
        return SkillType.Put_Into_Source;
    }

    @Override
    public List process(Skill skill, int roundIndex) {
        AbstractTrainerCreature myTrainer = skill.getSkillCaster().getMaster();
        AbstractTrainerCreature enemyTrainer = myTrainer.getRoom().getTrainers().stream()
                .filter(t -> t != myTrainer).findFirst().orElse(null);
        List<AbstractCreature> denfenders = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfenders)) {
            return null;
        }
        for (AbstractCreature denfender : denfenders) {
            MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(denfender);
            // 魔物娘扣血致死
            mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), skill.getSkillCaster(),
                    Reason.Put_Into_Source_Skill, true);
            if (RelationshipUtils.judgeRelationship(myTrainer, mwnCreature, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
                // 己方魔物娘返回己方牌库
                putIntoSource(myTrainer, mwnCreature);
            }
            if (RelationshipUtils.judgeRelationship(enemyTrainer, mwnCreature, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
                // 敌方魔物娘返回敌方牌库
                putIntoSource(Objects.requireNonNull(enemyTrainer), mwnCreature);
            }
            String format = String.format("魔物娘置入牌库技能, 技能ID:%s, 魔物娘配置ID:%s",
                    skill.getResource().getSkillId(), mwnCreature.getMwn().getResourceId());
            System.err.println(format);
        }
        return null;
    }

    private void putIntoSource(AbstractTrainerCreature trainer, MWNCreature mwnCreature) {
        List<MoWuNiang> result = new ArrayList<>();
        MoWuNiang mwn = mwnCreature.getMwn();
        trainer.getSourceCardStorage().addMwn(mwn);
        result.add(mwn);
        RoundFightUtils.trainerCardsChange(trainer, result, false, true, Reason.Put_Into_Source_Skill);
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
