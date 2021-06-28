package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.enums.RoomType;

/**
 * @author wang ke
 * @description:  玩家请求创建房间
 * @since 15:06 2020-08-14
 */
@SocketPacket(packetId = PacketId.PLAYER_ROOM_CREATE_REQ)
public class PlayerCreateRoomReq {

    @Protobuf(description = "房间类型")
    private RoomType roomType;

    @Protobuf(description = "驯养师Id")
    private long trainerId;

    @Protobuf(description = "房间名")
    private String name;

    @Protobuf(description = "地图Id")
    private int mapId;

    @Protobuf(description = "编队类型：1普通 2天梯" )
    private int troopType;

    @Protobuf(description = "选择技能")
    private int skillId;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getTroopType() {
        return troopType;
    }

    public void setTroopType(int troopType) {
        this.troopType = troopType;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
