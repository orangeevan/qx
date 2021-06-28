package com.mmorpg.qx.module.troop.packet.req;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 11:40 2021/4/8
 */
@SocketPacket(packetId = PacketId.UNLOCK_TROOP_REQ)
public class UnlockTroopReq {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    @Protobuf(description = "编队名字")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
