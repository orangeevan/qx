package com.mmorpg.qx.module.troop.packet.req;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.troop.model.Card;

import java.util.List;

/**
 * @author zhang peng
 * @description
 * @since 16:29 2021/4/8
 */
@SocketPacket(packetId = PacketId.ALTER_TROOP_REQ)
public class AlterTroopReq {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    @Protobuf(description = "更新后的卡牌组")
    private List<Card> cards;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
