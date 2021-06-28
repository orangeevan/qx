package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 18:48 2020-08-17
 */
@SocketPacket(packetId = PacketId.PLAYER_JOIN_ROOM_REQ)
public class PlayerJoinRoomReq {

    @Protobuf(description = "请求加入房间")
    private int roomId;

    @Protobuf(description = "驯养师Id")
    private long trainerId;

    @Protobuf(description = "编队类型：1普通 2天梯" )
    private int troopType;

    @Protobuf(description = "选择技能")
    private int skillId;

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
