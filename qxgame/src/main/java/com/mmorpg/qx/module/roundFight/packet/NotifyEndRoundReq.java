package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 客户端通知当前回合操作结束
 * @since 16:46 2020-10-12
 */
@SocketPacket(packetId = PacketId.NOTIFY_ROUND_END_REQ)
@ProtobufClass
public class NotifyEndRoundReq {

}
