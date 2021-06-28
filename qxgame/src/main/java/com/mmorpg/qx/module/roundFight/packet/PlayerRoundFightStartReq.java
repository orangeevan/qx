package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 玩家发起回合玩法战斗
 * @since 14:31 2020-08-14
 */
@SocketPacket(packetId = PacketId.PLAYER_ROUND_FIGHT_START_REQ)
public class PlayerRoundFightStartReq {
    @Protobuf(description = "房间号")
    private String room;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
