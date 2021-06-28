package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 19:28 2021/4/23
 */
@SocketPacket(packetId = PacketId.MWN_CHANGE_SKIN_REQ)
public class MwnChangeSkinReq {

    @Protobuf(description = "魔物娘ID")
    private long mwnId;

    @Protobuf(description = "皮肤配置ID")
    private int skinId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }
}
