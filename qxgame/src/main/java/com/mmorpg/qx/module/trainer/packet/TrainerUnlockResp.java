package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.packet.vo.PlayerTrainerVo;

/**
 * @author zhang peng
 * @description
 * @since 11:47 2021/4/7
 */
@SocketPacket(packetId = PacketId.TRAINER_UNLOCK_RESP)
public class TrainerUnlockResp {

    @Protobuf(description = "驯养师")
    private PlayerTrainerVo trainerVo;

    public static TrainerUnlockResp valueOf(PlayerTrainer playerTrainer) {
        TrainerUnlockResp resp = new TrainerUnlockResp();
        resp.trainerVo = PlayerTrainerVo.valueOf(playerTrainer);
        return resp;
    }

    public PlayerTrainerVo getTrainerVo() {
        return trainerVo;
    }

    public void setTrainerVo(PlayerTrainerVo trainerVo) {
        this.trainerVo = trainerVo;
    }
}
