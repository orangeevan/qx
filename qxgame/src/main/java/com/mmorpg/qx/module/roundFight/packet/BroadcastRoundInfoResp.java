package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;

/**
 * @author wang ke
 * @description:广播回合基础信息
 * @since 14:31 2020-08-14
 */
@SocketPacket(packetId = PacketId.BROADCAST_ROUND_INFO_RESP)
public class BroadcastRoundInfoResp {
    @Protobuf(description = "当前回合数")
    private int round;

    @Protobuf(description = "当前回合处于哪个阶段")
    private RoundStage stage;

    @Protobuf(description = "当前回合操作者")
    private long trainer;

    public static BroadcastRoundInfoResp valueOf(int round, RoundStage stage, long trainer) {
        BroadcastRoundInfoResp packet = new BroadcastRoundInfoResp();
        packet.round = round;
        packet.stage = stage;
        packet.trainer = trainer;
        return packet;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public RoundStage getStage() {
        return stage;
    }

    public void setStage(RoundStage stage) {
        this.stage = stage;
    }

    public long getTrainer() {
        return trainer;
    }

    public void setTrainer(long trainer) {
        this.trainer = trainer;
    }
}
