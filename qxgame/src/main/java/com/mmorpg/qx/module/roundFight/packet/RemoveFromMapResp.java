package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 对象移除出场景
 * @since 17:19 2020-07-31
 */
@SocketPacket(packetId = PacketId.REMOVE_CREATURE_FROM_MAP_RESP)
public class RemoveFromMapResp {
    @Protobuf(description = "对象移除出场景")
    private long objectId;

    public static RemoveFromMapResp valueOf(long objectId){
        RemoveFromMapResp removeMapResp = new RemoveFromMapResp();
        removeMapResp.objectId = objectId;
        return removeMapResp;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
