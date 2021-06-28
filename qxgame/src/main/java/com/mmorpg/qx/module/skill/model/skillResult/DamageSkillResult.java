package com.mmorpg.qx.module.skill.model.skillResult;

import com.mmorpg.qx.module.skill.model.Skill;

import java.util.ArrayList;
import java.util.List;

public class DamageSkillResult extends AbstractSkillResult {
//    /**
//     * 实际伤害值
//     */
//    @Protobuf
//    private int value;
//
//    /**
//     * 吸血
//     */
//    @Protobuf
//    private int suck;
//
//    /**
//     * 减伤
//     */
//    @Protobuf
//    private int avoidHarm;

    public static DamageSkillResult valueOf(long castId, long defenderId, int value, int skillId) {
        DamageSkillResult result = valueOf(castId, defenderId, null);
        result.setSkillId(skillId);
        result.setValue(value);
        return result;
    }

    public static DamageSkillResult valueOf(long attackerId, long defenderId, Skill skill) {
        DamageSkillResult damage = new DamageSkillResult(skill, attackerId, defenderId, 0);
        return damage;
    }

    public static DamageSkillResult valueOf(long attackerId, long defenderId, Skill skill, int effectId) {
        List<Integer> effectIds = new ArrayList<>(1);
        if (effectId > 0) {
            effectIds.add(effectId);
        }
        return valueOf(attackerId, defenderId, skill);
    }

    public DamageSkillResult(Skill skill, long attackerId, long defenderId, int roundIndex) {
        super(skill, defenderId, roundIndex, attackerId);
    }

    public DamageSkillResult() {
        super();
    }

    public int getSuck() {
        return getValue2();
    }

    public void setSuck(int suck) {
        setValue2(suck);
    }

    public int getAvoidHarm() {
        return getValue3();
    }

    public void setAvoidHarm(int avoidHarm) {
        setValue3(avoidHarm);
    }

    public int getValue() {
        return getValue1();
    }

    public void setValue(int value) {
        this.setValue1(value);
    }

    @Override
    public SkillResultType getType() {
        return SkillResultType.Damage;
    }
}
