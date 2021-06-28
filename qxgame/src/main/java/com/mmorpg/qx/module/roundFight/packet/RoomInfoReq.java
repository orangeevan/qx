package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.roundFight.enums.RoomState;

/**
 * @author wang ke
 * @description: 玩家请求系统房间信息
 * @since 19:12 2020-08-17
 */
@SocketPacket(packetId = PacketId.ROOM_INFO_REQ)
@ProtobufClass
public class RoomInfoReq {

}
