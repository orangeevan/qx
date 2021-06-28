package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 玩家请求投骰子
 * @since 16:58 2020-08-14
 */
@SocketPacket(packetId = PacketId.PLAYER_THROW_DICE_REQ)
@ProtobufClass
public class PlayerThrowDiceReq {

}
