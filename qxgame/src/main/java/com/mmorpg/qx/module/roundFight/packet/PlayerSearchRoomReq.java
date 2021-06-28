package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 查询指定房间信息
 * @since 13:40 2020-08-18
 */
@SocketPacket(packetId = PacketId.PLAYER_SEARCH_ROOM_REQ)
public class PlayerSearchRoomReq {

    @Protobuf(description = "房间号")
    private int roomId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
