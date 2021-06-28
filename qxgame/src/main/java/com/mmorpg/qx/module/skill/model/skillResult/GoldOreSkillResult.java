package com.mmorpg.qx.module.skill.model.skillResult;

import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author wang ke
 * @description: 金矿技能结果
 * @since 16:28 2020-09-02
 */
public class GoldOreSkillResult extends AbstractSkillResult {
//    @Protobuf
//    private int gold;

    public int getGold() {
        return getValue1();
    }

    public void setGold(int gold) {
        setValue1(gold);
    }

    public GoldOreSkillResult(long attackerId, long defenderId, Skill skill, int gold, int roundIndex) {
        super(skill, defenderId, roundIndex, attackerId);
        setGold(gold);
    }

    public GoldOreSkillResult() {
        super();
    }

    @Override
    public SkillResultType getType() {
        return SkillResultType.GoldOre;
    }
}
