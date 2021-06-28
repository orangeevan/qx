package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 请求离线回合战斗日志
 * @since 17:26 2021/1/6
 */
@SocketPacket(packetId = PacketId.PLAYER_OFF_LINE_ROUND_LOG_REQ)
@ProtobufClass
public class OffLineRoundLogReq {

}
