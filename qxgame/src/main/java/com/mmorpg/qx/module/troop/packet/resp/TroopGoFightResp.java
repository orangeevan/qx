package com.mmorpg.qx.module.troop.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 20:42 2021/4/19
 */
@SocketPacket(packetId = PacketId.TROOP_GO_FIGHT_RESP)
public class TroopGoFightResp {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    public static TroopGoFightResp valueOf(int type, int index) {
        TroopGoFightResp resp = new TroopGoFightResp();
        resp.type = type;
        resp.index = index;
        return resp;
    }

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
