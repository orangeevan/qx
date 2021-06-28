package com.mmorpg.qx.module.troop.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 10:59 2021/4/29
 */
@SocketPacket(packetId = PacketId.TROOP_CHANGE_SKILL_RESP)
public class TroopChangeSkillResp {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    @Protobuf(description = "编队技能")
    private int skillId;

    public static TroopChangeSkillResp valueOf(int type, int index, int skillId) {
        TroopChangeSkillResp resp = new TroopChangeSkillResp();
        resp.type = type;
        resp.index = index;
        resp.skillId = skillId;
        return resp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
