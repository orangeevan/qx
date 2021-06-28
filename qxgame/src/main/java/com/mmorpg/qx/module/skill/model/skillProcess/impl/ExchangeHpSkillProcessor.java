package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.RecoverSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.model.target.TargetType;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:换血技能
 * @since 15:49 2020-09-10
 */
public class ExchangeHpSkillProcessor extends AbstractSkillProcessor {

    @Override
    public SkillType getSkillType() {
        return SkillType.Exchange_Hp;
    }

    @Override
    public List<RecoverSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfendersList = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfendersList)) {
            return null;
        }
        AbstractTrainerCreature master = skill.getSkillCaster().getMaster();
        Collection<MWNCreature> mwnCreatures = master.getMWN(true);
        if (CollectionUtils.isEmpty(mwnCreatures)) {
            return null;
        }
        MWNCreature minHp = mwnCreatures.stream().min(Comparator.comparingInt(MWNCreature::getCurHpPercent)).get();
        MWNCreature maxHp = null;
        if (!CollectionUtils.isEmpty(skill.getDenfendersList())) {
            AbstractCreature creature = skill.getDenfendersList().get(0);
            maxHp = (MWNCreature) creature;
        } else {
//            ITargetChooser targetChooser = TargetManager.getInstance().getTargetChooser(skill.getResource().getTargetType());
//            Target target = targetChooser.chooseTarget(skill.getSkillCaster());
            Target target = TargetManager.getInstance().chooseTarget(new ArrayList<>(skill.getResource().getTargets().keySet()), skill.getSkillCaster());
            List<Long> targetIds = target.getTargetIds();
            if (CollectionUtils.isEmpty(targetIds)) {
                return null;
            }
            AbstractVisibleObject object = skill.getSkillCaster().getWorldMapInstance().findObject(targetIds.get(0));
            maxHp = ((MWNCreature) object);
        }
        int minPercent = minHp.getCurHpPercent();
        int maxPercent = maxHp.getCurHpPercent();
        int minPercentHp = minHp.getLifeStats().getMaxHp() * maxPercent;
        int maxPercentHp = maxHp.getLifeStats().getMaxHp() * minPercent;
        minHp.getLifeStats().increaseHp(minPercentHp - minHp.getCurrentHp(), Reason.Trainer_Skill_Effect, false, true);
        maxHp.getLifeStats().reduceHp(maxHp.getCurrentHp() - maxPercentHp, skill.getSkillCaster(), skill.getResource().getSkillId(), 0);
        RecoverSkillResult result = new RecoverSkillResult(master.getObjectId(), maxHp.getObjectId(), skill, minPercentHp - minHp.getCurrentHp(), 0);
        List<RecoverSkillResult> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
