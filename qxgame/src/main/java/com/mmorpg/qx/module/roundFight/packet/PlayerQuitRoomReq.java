package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 玩家中途退出房间
 * @since 15:26 2020-09-23
 */
@SocketPacket(packetId = PacketId.PLAYER_QUIT_ROOM_REQ)
@ProtobufClass
public class PlayerQuitRoomReq {

}
