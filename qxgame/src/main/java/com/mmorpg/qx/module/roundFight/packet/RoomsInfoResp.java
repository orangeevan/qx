package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.packet.vo.RoomVo;

import java.util.List;

/**
 * @author wang ke
 * @description:
 * @since 19:13 2020-08-17
 */
@SocketPacket(packetId = PacketId.ROOM_INFO_RESP)
public class RoomsInfoResp {

    @Protobuf(description = "房间信息")
    private List<RoomVo> roomInfo;

    public static RoomsInfoResp valueOf(List<RoomVo> roomInfo) {
        RoomsInfoResp roomInfoResp = new RoomsInfoResp();
        roomInfoResp.setRoomInfo(roomInfo);
        return roomInfoResp;
    }

    public List<RoomVo> getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(List<RoomVo> roomInfo) {
        this.roomInfo = roomInfo;
    }
}
