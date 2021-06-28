package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.packet.vo.TrainerOrderVo;

import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 驯养师开局顺序信息
 * @since 11:08 2021/3/17
 */
@SocketPacket(packetId = PacketId.TRAINER_ORDER_INFO_RESP)
public class TrainerOrderInfoResp {

    @Protobuf(description = "驯养师Id,点子数键值对")
    private List<TrainerOrderVo> ordersInfo;

    public static TrainerOrderInfoResp valueOf(Map<Long, Integer> trainerOrderInfo) {
        TrainerOrderInfoResp orderInfoResp = new TrainerOrderInfoResp();
        orderInfoResp.ordersInfo = TrainerOrderVo.buildTrainerOrderInfo(trainerOrderInfo);
        return orderInfoResp;
    }

    public List<TrainerOrderVo> getOrdersInfo() {
        return ordersInfo;
    }

    public void setOrdersInfo(List<TrainerOrderVo> ordersInfo) {
        this.ordersInfo = ordersInfo;
    }
}
