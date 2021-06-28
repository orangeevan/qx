package com.mmorpg.qx.module.troop.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.troop.model.Troop;

/**
 * @author zhang peng
 * @description
 * @since 17:46 2021/4/8
 */
@SocketPacket(packetId = PacketId.UPDATE_TROOP_RESP)
public class UpdateTroopResp {

    @Protobuf(description = "更新后编队信息")
    private Troop troop;

    public static UpdateTroopResp valueOf(Troop troop) {
        UpdateTroopResp resp = new UpdateTroopResp();
        resp.troop = troop;
        return resp;
    }

    public Troop getTroop() {
        return troop;
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
    }
}
