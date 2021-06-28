package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 11:53 2020-08-11
 */
@SocketPacket(packetId = PacketId.PLAYER_HEARTBEAT_RESP)
@ProtobufClass
public class PlayerHeartBeatResp {

}
