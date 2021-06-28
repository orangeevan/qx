package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;

/**
 * @author wang ke
 * @description: 技能释放完后CD
 * @since 17:57 2020-12-01
 */
@SocketPacket(packetId = PacketId.SINGLE_SKILL_CD_RESP)
public class SingleSkillCdResp {
    @Protobuf(description = "技能数据CD")
    private SkillVo skill;

    public static SingleSkillCdResp valueOf(AbstractCreature creature, int skillId){
        SingleSkillCdResp resp = new SingleSkillCdResp();
        resp.skill = SkillVo.valueOf(creature, skillId);
        return resp;
    }

    public SkillVo getSkill() {
        return skill;
    }

    public void setSkill(SkillVo skill) {
        this.skill = skill;
    }
}
