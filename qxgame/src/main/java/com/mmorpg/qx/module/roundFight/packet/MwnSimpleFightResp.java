package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 广播魔物娘简易战斗模式
 * @since 16:11 2020-10-19
 */
@SocketPacket(packetId = PacketId.MWN_SIMPLE_FIGHT_RESP)
public class MwnSimpleFightResp {
    @Protobuf(description = "1开启 0未开启")
    private int open;

    public static MwnSimpleFightResp valueOf(int open) {
        MwnSimpleFightResp resp = new MwnSimpleFightResp();
        resp.open = open;
        return resp;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
