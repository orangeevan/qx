package com.mmorpg.qx.module.gm.packet;


import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wangke
 * @since v1.0 2019/4/2
 */
@SocketPacket(packetId = PacketId.SERVER_KICK_OFF)
public class ServerKickOff {
    @Protobuf
    private long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
