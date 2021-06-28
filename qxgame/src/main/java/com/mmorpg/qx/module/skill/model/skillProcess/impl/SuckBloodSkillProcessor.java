package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author zhang peng
 * @description 吸血 魔物娘技能
 * 技能对目标对象释放后，敌方驯养师扣除生命值，恢复己方驯养师等量配置值的生命值，但回复后的生命值不可高过生命值上限
 * @since 10:31 2021/4/15
 */
@Component
public class SuckBloodSkillProcessor extends AbstractSkillProcessor<AbstractSkillResult, Map<String, Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Suck_Blood;
    }

    @Override
    public List<AbstractSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfenders = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfenders)) {
            return null;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(skill.getSkillCaster());
        Map<String, Integer> param = initParam(skill.getResource());
        //mode:1释放者武力值2具体数值  value:数值大小
        Integer mode = param.get("mode");
        Integer value = param.get("value");
        if (mode != 1 && mode != 2) {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
        int hp = mode == 1 ? mwnCreature.getAttrController().getCurrentAttr(AttrType.Trainer_Harm) : value;
        for (AbstractCreature defender : denfenders) {
            int defCurrentHp = defender.getMaster().getLifeStats().getCurrentHp();
            int finalHp = Math.min(hp, defCurrentHp);
            // 己方驯养师加血
            int maxHp = mwnCreature.getMaster().getAttrController().getCurrentAttr(AttrType.Max_Hp);
            int currentHp = mwnCreature.getMaster().getLifeStats().getCurrentHp();
            int addHP = Math.min(maxHp - currentHp, finalHp);
            mwnCreature.getMaster().getLifeStats().increaseHp(addHP, Reason.Suck_Blood_Skill, true, true);
            // 敌方驯养师扣血
            defender.getMaster().getLifeStats().reduceHp(finalHp, skill.getSkillCaster(), Reason.Suck_Blood_Skill, true);
            String format = String.format("吸血技能, 技能ID:%s, mode:%s, value:%s", skill.getResource().getSkillId(), mode, value);
            System.err.println(format);
        }
        return null;
    }

    @Override
    public Map<String, Integer> initParam(SkillResource resource) {
        return JsonUtils.string2Map(resource.getParam(), String.class, Integer.class);
    }

}
