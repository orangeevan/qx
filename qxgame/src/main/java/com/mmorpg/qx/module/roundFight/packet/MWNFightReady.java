package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 17:34 2020-08-29
 */
@SocketPacket(packetId = PacketId.MWN_FIGHT_READY_REQ)
public class MWNFightReady {
    @Protobuf(description = "魔物娘id")
    private long mwnId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }
}
