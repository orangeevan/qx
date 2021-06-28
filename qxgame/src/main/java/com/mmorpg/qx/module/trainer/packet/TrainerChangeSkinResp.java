package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 19:22 2021/4/7
 */
@SocketPacket(packetId = PacketId.TRAINER_CHANGE_SKIN_RESP)
public class TrainerChangeSkinResp {

    @Protobuf(description = "驯养师ID")
    private long trainerId;

    @Protobuf(description = "皮肤配置ID")
    private int skinId;

    public static TrainerChangeSkinResp valueOf(long trainerId, int skinId) {
        TrainerChangeSkinResp resp = new TrainerChangeSkinResp();
        resp.trainerId = trainerId;
        resp.skinId = skinId;
        return resp;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }
}
