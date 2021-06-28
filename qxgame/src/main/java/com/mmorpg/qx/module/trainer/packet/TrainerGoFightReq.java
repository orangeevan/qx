package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 20:04 2021/4/14
 */
@SocketPacket(packetId = PacketId.TRAINER_GO_FIGHT_REQ)
public class TrainerGoFightReq {

    @Protobuf(description = "驯养师ID")
    private long trainerId;

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }
}
