package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description:
 * @since 15:39 2021/3/11
 */
@SocketPacket(packetId = PacketId.DEVELOP_MWN_REQ)
public class DevelopMwnReq {

    @Protobuf(description = "魔物娘id")
    private long mwnId;
    @Protobuf(description = "培养类型 1升级 2升阶 3升星")
    private int type;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
