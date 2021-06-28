package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author wang ke
 * @description: 魔物娘战斗开启
 * @since 17:37 2020-08-28
 */
//@SocketPacket(packetId = PacketId.MWN_FIGHT_REQ)
public class MwnFightStartReq {
    @Protobuf
    private long mwnId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }
}
