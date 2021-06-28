package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.target.Target;

/**
 * 使用技能请求
 *
 * @author wang ke
 * @since v1.0 2018年3月19日
 */
@SocketPacket(packetId = PacketId.USE_SKILL_REQ)
public class UseSkillReq {
    @Protobuf(description = "施法者，驯养师或者魔物娘")
    private long objectId;

    @Protobuf
    private int skillId;

    @Protobuf
    private Target target;

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
