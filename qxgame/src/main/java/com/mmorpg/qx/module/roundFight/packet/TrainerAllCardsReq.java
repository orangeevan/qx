package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:驯养师所有卡牌信息
 * @since 11:37 2020/12/14
 */
@SocketPacket(packetId = PacketId.TRAINER_ALL_CARDS_REQ)
@ProtobufClass
public class TrainerAllCardsReq {

}
