package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 魔物娘援助
 * @since 20:16 2021/3/18
 */
@SocketPacket(packetId = PacketId.MWN_SUPPORT_REQ)
public class MwnSupportReq {
    @Protobuf(description = "援助者Id")
    private long supportId;

    public long getSupportId() {
        return supportId;
    }

    public void setSupportId(long supportId) {
        this.supportId = supportId;
    }
}
