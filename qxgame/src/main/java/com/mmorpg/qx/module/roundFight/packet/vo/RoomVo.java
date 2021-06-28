package com.mmorpg.qx.module.roundFight.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.roundFight.enums.RoomState;
import com.mmorpg.qx.module.roundFight.enums.RoomType;

/**
 * @author wang ke
 * @description: 房间信息，后面再添加详细信息
 * @since 19:14 2020-08-17
 */
public class RoomVo {

    @Protobuf(description = "房间号", order = 1)
    private int roomId;
    @Protobuf(description = "创建者", order = 2)
    private long createrId;
    @Protobuf(description = "创建者", order = 3)
    private String createrName;
    @Protobuf(description = "房间类型", order = 4)
    private RoomType roomType;

    @Protobuf(description = "房间最大人数", order = 5)
    private int maxMember;

    @Protobuf(description = "房间当前人数", order = 6)
    private int currentMember;

    @Protobuf(description = "房间状态", order = 7)
    private RoomState roomState;

    @Protobuf(description = "地图数据")
    private int mapId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(long createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public RoomState getRoomState() {
        return roomState;
    }

    public void setRoomState(RoomState roomState) {
        this.roomState = roomState;
    }

    public int getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(int currentMember) {
        this.currentMember = currentMember;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
