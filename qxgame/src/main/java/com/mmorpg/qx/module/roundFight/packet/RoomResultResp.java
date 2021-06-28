package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.model.Room;

/**
 * @author wang ke
 * @description: 房间战斗结果返回
 * @since 10:49 2020-11-25
 */
@SocketPacket(packetId = PacketId.ROOM_FIGHT_RESULT_RESP)
public class RoomResultResp {

    @Protobuf(description = "胜利方")
    private long winner;

    public static RoomResultResp valueOf(Room room) {
        RoomResultResp resp = new RoomResultResp();
        resp.setWinner(room.getWinner() == null ? 0 : room.getWinner().getObjectId());
        return resp;
    }

    public long getWinner() {
        return winner;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }
}
