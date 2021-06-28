package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

import java.util.List;

@SocketPacket(packetId = PacketId.MOVE_RESP)
public class MoveResp {
    @Protobuf(description = "对象id")
    private long objId;
    @Protobuf(description = "格子号")
    private int gridId;
    @Protobuf(description = "方向列表代表路径")
    private List<GameUtil.IntegerVo> roads;

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public List<GameUtil.IntegerVo> getRoads() {
        return roads;
    }

    public void setRoads(List<GameUtil.IntegerVo> roads) {
        this.roads = roads;
    }

    public static MoveResp valueOf(AbstractCreature creature, int gridId, byte[] roads) {
        MoveResp result = new MoveResp();
        result.setGridId(gridId);
        result.objId = creature.getObjectId();
        result.roads = GameUtil.toIntegerVo(roads);
        return result;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }
}
