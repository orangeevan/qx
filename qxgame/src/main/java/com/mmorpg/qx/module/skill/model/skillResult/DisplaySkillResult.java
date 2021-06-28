package com.mmorpg.qx.module.skill.model.skillResult;

import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author wang ke
 * @description: 技能播放类结果
 * @since 15:07 2020-11-11
 */
public class DisplaySkillResult extends AbstractSkillResult {

    @Override
    public SkillResultType getType() {
        return SkillResultType.Display_Skill;
    }

    public static DisplaySkillResult valueOf(long defenderId, Skill skill) {
        return new DisplaySkillResult(skill, defenderId, 0);
    }

    public DisplaySkillResult() {}

    public DisplaySkillResult(Skill skill, long defenderId,  int roundIndex) {
        super(skill, defenderId, roundIndex, skill.getSkillCaster().getObjectId());
    }
}
