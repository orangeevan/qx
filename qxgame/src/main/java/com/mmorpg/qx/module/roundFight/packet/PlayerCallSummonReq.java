package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 驯养师召唤魔物娘
 * @since 15:06 2020-08-14
 */
@SocketPacket(packetId = PacketId.PLAYER_CALL_SUMMON_REQ)
public class PlayerCallSummonReq {

    @Protobuf(description = "魔物娘id")
    private long mwnId;

    @Protobuf(description = "目标格子号")
    private int gridId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }
}

