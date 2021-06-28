package com.mmorpg.qx.module.skill.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.resource.SkillResource;

/**
 * @author wang ke
 * @description: 技能数据
 * @since 15:19 2020-10-10
 */
public class SkillVo {

    @Protobuf(description = "技能组id")
    private int skillGroupId;

//    @Protobuf(description = "回合cd")
//    private int roundCd;

    @Protobuf(description = "回合公共cd")
    private int pubRoundCd;

    public int getSkillGroupId() {
        return skillGroupId;
    }

    public void setSkillGroupId(int skillGroupId) {
        this.skillGroupId = skillGroupId;
    }

//    public int getRoundCd() {
//        return roundCd;
//    }
//
//    public void setRoundCd(int roundCd) {
//        this.roundCd = roundCd;
//    }

    public int getPubRoundCd() {
        return pubRoundCd;
    }

    public void setPubRoundCd(int pubRoundCd) {
        this.pubRoundCd = pubRoundCd;
    }

    public static SkillVo valueOf(AbstractCreature creature, int skillId){
        SkillVo vo = new SkillVo();
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        vo.setSkillGroupId(skillResource.getPublicGroup());
        //vo.setRoundCd(creature.getSkillController().getSKillCD(skillId));
        vo.setPubRoundCd(creature.getSkillController().getSkillGroupCD(skillId));
        return vo;
    }
}
