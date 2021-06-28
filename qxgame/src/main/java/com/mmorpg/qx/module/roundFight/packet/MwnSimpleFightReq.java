package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 请求设置简易战斗模式
 * @since 16:09 2020-10-19
 */
@SocketPacket(packetId = PacketId.MWN_SIMPLE_FIGHT_REQ)
public class MwnSimpleFightReq {
    @Protobuf(description = "1开启 0关闭")
    private int openOrClose;

    public int getOpenOrClose() {
        return openOrClose;
    }

    public void setOpenOrClose(int openOrClose) {
        this.openOrClose = openOrClose;
    }
}
