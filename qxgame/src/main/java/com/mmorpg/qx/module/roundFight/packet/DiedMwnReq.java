package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 请求死亡魔物娘
 * @since 17:31 2020-11-19
 */
@SocketPacket(packetId = PacketId.DIED_MWN_REQ)
@ProtobufClass
public class DiedMwnReq {

}
