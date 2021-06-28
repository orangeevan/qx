package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 对象身上buff信息
 * @since 14:19 2020-10-20
 */
@SocketPacket(packetId = PacketId.CREATURE_EFFECT_INFO_REQ)
public class CreatureEffectReq {

    @Protobuf(description = "对象id，一般是自身id")
    private long objectId;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
