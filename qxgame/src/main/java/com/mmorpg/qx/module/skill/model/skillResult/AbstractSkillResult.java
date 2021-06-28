package com.mmorpg.qx.module.skill.model.skillResult;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.skill.model.FightStatus;
import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author wang ke
 * @description: 技能处理结果
 * @since 19:48 2020-08-31
 */
public abstract class AbstractSkillResult {
    /**
     * 结果类型
     */
    @Protobuf(description = "结果类型,不同类型数据结构不同", order = 1)
    private SkillResultType type;

    /**
     * 攻击方
     */
    @Protobuf(description = "攻击方", order = 2)
    private long castId;

    /**
     * 防守方
     */
    @Protobuf(description = "防守方", order = 3)
    private long targetId;

    /**
     * 暴击闪避
     */
    @Protobuf(description = "技能状态，普通攻击，暴击，闪避，减伤等", order = 4)
    private FightStatus status = FightStatus.NORMAL;

    /**
     * 技能id
     */
    @Protobuf(description = "技能id", order = 5)
    private int skillId;

    /**
     * 回合次序
     */
    @Protobuf(order = 7)
    private int roundIndex;

    @Protobuf(description = "type =0 实际伤害值；type =1 金币；type =2 恢复血量 ", order = 8)
    private int value1;

    @Protobuf(description = "type =0 吸血；type =1 ；type =2 恢复魔法", order = 9)
    private int value2;

    @Protobuf(description = "type =0 减伤；type =1 ；type =2 ", order = 10)
    private int value3;

    public long getCastId() {
        return castId;
    }

    public void setCastId(long castId) {
        this.castId = castId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public FightStatus getStatus() {
        return status;
    }

    public void setStatus(FightStatus status) {
        this.status = status;
    }


    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(int roundIndex) {
        this.roundIndex = roundIndex;
    }

    public abstract SkillResultType getType();

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public int getValue3() {
        return value3;
    }

    public void setValue3(int value3) {
        this.value3 = value3;
    }

    public AbstractSkillResult(Skill skill, long targetId, int roundIndex, long castId) {
        this();
        this.castId = castId;
        this.targetId = targetId;
        this.skillId = skill != null ? skill.getResource().getSkillId() : 0;
        this.roundIndex = roundIndex;
    }

    public AbstractSkillResult() {
        this.type = getType();
    }

    @Override
    public String toString() {
        return "AbstractSkillResult{" +
                "type=" + type +
                ", castId=" + castId +
                ", targetId=" + targetId +
                ", status=" + status +
                ", skillId=" + skillId +
                ", roundIndex=" + roundIndex +
                ", value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                '}';
    }
}
