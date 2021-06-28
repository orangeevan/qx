package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.UPDATE_POSITION)
public class UpdatePositionResp {

    @Protobuf(description = "对象id")
    private long objectId;

    @Protobuf(description = "地图Id")
    private int mapId;

    @Protobuf(description = "地图格子编号")
    private int gridId;


    public static UpdatePositionResp valueOf(long objectId, int mapId, int gridId) {
        UpdatePositionResp sm = new UpdatePositionResp();
        sm.objectId = objectId;
        sm.mapId = mapId;
        sm.gridId = gridId;
        return sm;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }
}
