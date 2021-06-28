package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 请求抽卡
 * @since 21:07 2020-10-20
 */
@SocketPacket(packetId = PacketId.PLAYER_EXTRACT_CARD_REQ)
@ProtobufClass
public class TrainerExtractCardReq {

}
