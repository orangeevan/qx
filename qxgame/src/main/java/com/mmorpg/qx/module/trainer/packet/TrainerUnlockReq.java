package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 11:01 2021/4/7
 */
@SocketPacket(packetId = PacketId.TRAINER_UNLOCK_REQ)
public class TrainerUnlockReq {

    @Protobuf(description = "驯养师配置ID")
    private int resourceId;

    @Protobuf(description = "驯养师碎片ID")
    private long chipId;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public long getChipId() {
        return chipId;
    }

    public void setChipId(long chipId) {
        this.chipId = chipId;
    }
}
