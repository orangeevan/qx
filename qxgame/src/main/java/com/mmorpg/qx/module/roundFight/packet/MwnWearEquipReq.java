package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 11:40 2020-08-28
 */
@SocketPacket(packetId = PacketId.MWN_WEAR_EQUIP_REQ)
public class MwnWearEquipReq {

    @Protobuf(description = "魔物娘Id")
    private long mwnId;

    @Protobuf(description = "装备id")
    private long equipId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public long getEquipId() {
        return equipId;
    }

    public void setEquipId(long equipId) {
        this.equipId = equipId;
    }
}
