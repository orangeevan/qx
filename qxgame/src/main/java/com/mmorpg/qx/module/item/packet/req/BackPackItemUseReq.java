package com.mmorpg.qx.module.item.packet.req;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 11:46 2020-08-07
 */
@SocketPacket(packetId = PacketId.BACKPACK_ITEM_USE)
public class BackPackItemUseReq {

    @Protobuf(description = "物品唯一ID")
    private long objectId;

    @Protobuf(description = "物品使用数量")
    private int num;

    @Protobuf(description = "物品使用方法 1出售 2 使用")
    private int use;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
    }
}
