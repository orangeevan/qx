package com.mmorpg.qx.module.skill.model.skillResult;

import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author zhang peng
 * @description
 * @since 11:48 2021/4/12
 */
public class ChangeAttrResult extends AbstractSkillResult {

    @Override
    public SkillResultType getType() {
        return SkillResultType.Change_Attr;
    }

    public ChangeAttrResult() {}

    public ChangeAttrResult(long attackerId, long defenderId, int roundIndex, Skill skill, int oldValue, int newValue) {
        super(skill, defenderId, roundIndex, attackerId);
        // 属性旧值
        this.setValue1(oldValue);
        // 属性新值
        this.setValue2(newValue);
    }
}
