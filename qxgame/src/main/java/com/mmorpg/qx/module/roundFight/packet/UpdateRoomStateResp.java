package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.packet.vo.RoomVo;

/**
 * @author wang ke
 * @description: 房间状态更新
 * @since 20:47 2020-08-17
 */
@SocketPacket(packetId = PacketId.ROOM_UPDATE_RESP)
public class UpdateRoomStateResp {

    @Protobuf(description = "房间信息")
    RoomVo roomVo;

    public static UpdateRoomStateResp valueOf(RoomVo roomVo) {
        UpdateRoomStateResp updateRoomStateResp = new UpdateRoomStateResp();
        updateRoomStateResp.setRoomVo(roomVo);
        return updateRoomStateResp;
    }

    public RoomVo getRoomVo() {
        return roomVo;
    }

    public void setRoomVo(RoomVo roomVo) {
        this.roomVo = roomVo;
    }
}
