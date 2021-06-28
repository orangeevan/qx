package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:消耗魔物娘卡牌投骰子即魔物娘投骰子
 * @since 15:02 2020-09-14
 */
@SocketPacket(packetId = PacketId.COST_CARD_THROW_DICE)
public class CostCardDiceReq {

    @Protobuf(description = "魔物娘id")
    private long mwnId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }
}
