package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhang peng
 * @description 净化效果技能
 * @since 20:14 2021/4/15
 */
@Component
public class PurifyEffectSkillProcessor extends AbstractSkillProcessor<AbstractSkillResult, List<Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Purify_Effect;
    }

    @Override
    public List<AbstractSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfenders = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfenders)) {
            return null;
        }
        List<Integer> buffTypes = initParam(skill.getResource());
        // 净化的Effect类型 1buff 2debuff
        for (AbstractCreature defender : denfenders) {
            buffTypes.forEach(buffType -> {
                defender.getEffectController().removeEffects(buffType);
                String format = String.format("净化效果技能, 技能ID:%s, buffType:%s", skill.getResource().getSkillId(), buffType);
                System.err.println(format);
            });
        }
        return null;
    }

    @Override
    public List<Integer> initParam(SkillResource resource) {
        return JsonUtils.string2List(resource.getParam(), Integer.class);
    }
}
