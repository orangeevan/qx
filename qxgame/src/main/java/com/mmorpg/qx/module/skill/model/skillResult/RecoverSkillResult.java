package com.mmorpg.qx.module.skill.model.skillResult;

import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author wang ke
 * @description: 恢复技能处理结果
 * @since 20:10 2020-08-31
 */
public class RecoverSkillResult extends AbstractSkillResult {
//    @Protobuf(description = "恢复血量")
//    private int recoverHp;
//
//    @Protobuf(description = "恢复魔法")
//    private int recoverMp;

    public int getRecoverHp() {
        return getValue1();
    }

    public void setRecoverHp(int recoverHp) {
        this.setValue1(recoverHp);
    }

    public int getRecoverMp() {
        return getValue2();
    }

    public void setRecoverMp(int recoverMp) {
        this.setValue2(recoverMp);
    }

    public RecoverSkillResult(long attackerId, long defenderId, Skill skill, int recoverHp, int recoverMp) {
        super(skill, defenderId, 0, attackerId);
        setRecoverHp(recoverHp);
        setRecoverMp(recoverMp);
    }

    public RecoverSkillResult() {
        super();
    }

    @Override
    public SkillResultType getType() {
        return SkillResultType.Recover;
    }
}
