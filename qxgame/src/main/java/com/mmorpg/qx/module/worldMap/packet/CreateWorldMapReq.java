package com.mmorpg.qx.module.worldMap.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 创建地图，测试用
 * @since 17:20 2020-08-04
 */
@SocketPacket(packetId = PacketId.CREATE_MAP_REQ)
public class CreateWorldMapReq {

    @Protobuf
    private int mapId;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
