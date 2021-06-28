package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wang ke
 * @description: 没有处理过程技能类型
 * @since 16:54 2020-09-07
 */
@Component
public class NoneSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, Object> {

    @Override
    public SkillType getSkillType() {
        return SkillType.None;
    }

    @Override
    public List process(Skill skill, int roundIndex) {
        return null;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
