package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.model.DicePoint;

/**
 * @author wang ke
 * @description: 返回投骰子结果
 * @since 17:02 2020-08-14
 */
@SocketPacket(packetId = PacketId.THROW_DICE_RESP)
public class ThrowDiceResp {

    @Protobuf(description = "投骰子驯养师")
    private long trainerId;
    @Protobuf(description = "骰子点数")
    private DicePoint dicePoint;

    public static ThrowDiceResp valueOf(long trainerId, DicePoint dicePoint) {
        ThrowDiceResp diceResp = new ThrowDiceResp();
        diceResp.setTrainerId(trainerId);
        diceResp.setDicePoint(dicePoint);
        return diceResp;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public DicePoint getDicePoint() {
        return dicePoint;
    }

    public void setDicePoint(DicePoint dicePoint) {
        this.dicePoint = dicePoint;
    }
}
