package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import com.mmorpg.qx.module.skill.packet.vo.TrainerBuildCDVo;

import java.util.List;

/**
 * @author wang ke
 * @description: 技能建筑cd数据
 * @since 20:36 2020-12-01
 */
@SocketPacket(packetId = PacketId.ALL_SKILL_BUILD_CD_RESP)
public class SkillBuildCdResp {
    @Protobuf(description = "技能CD")
    private List<SkillVo> skills;

    @Protobuf(description = "建筑CD")
    private List<TrainerBuildCDVo> build;

    public static SkillBuildCdResp valueOf(AbstractCreature creature) {
        SkillBuildCdResp resp = new SkillBuildCdResp();
        resp.skills = creature.getSkillController().getSkillVo();
        resp.build = creature.getSkillController().getBuildVo();
        return resp;
    }

    public List<SkillVo> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillVo> skills) {
        this.skills = skills;
    }

    public List<TrainerBuildCDVo> getBuild() {
        return build;
    }

    public void setBuild(List<TrainerBuildCDVo> build) {
        this.build = build;
    }
}
