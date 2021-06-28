package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.trainer.packet.vo.PlayerTrainerVo;

import java.util.List;

/**
 * @author zhang peng
 * @description
 * @since 10:03 2021/4/7
 */
@SocketPacket(packetId = PacketId.TRAINER_LIST_RESP)
public class TrainerListResp {

    @Protobuf(description = "驯养师列表")
    private List<PlayerTrainerVo> trainerList;

    public List<PlayerTrainerVo> getTrainerList() {
        return trainerList;
    }

    public void setTrainerList(List<PlayerTrainerVo> trainerList) {
        this.trainerList = trainerList;
    }
}
