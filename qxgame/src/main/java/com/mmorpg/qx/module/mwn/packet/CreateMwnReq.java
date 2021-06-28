package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 12:04 2020-08-19
 */
@SocketPacket(packetId = PacketId.CREATE_MWN_REQ)
public class CreateMwnReq {
    @Protobuf
    private int mwnId;

    public int getMwnId() {
        return mwnId;
    }

    public void setMwnId(int mwnId) {
        this.mwnId = mwnId;
    }
}
