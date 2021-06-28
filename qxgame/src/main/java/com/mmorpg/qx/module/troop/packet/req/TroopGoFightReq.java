package com.mmorpg.qx.module.troop.packet.req;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 20:42 2021/4/19
 */
@SocketPacket(packetId = PacketId.TROOP_GO_FIGHT_REQ)
public class TroopGoFightReq {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

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
}
