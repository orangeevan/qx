package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 驯养师进入场景后加载完毕准备开启战斗
 * @since 17:10 2020-10-22
 */
@SocketPacket(packetId = PacketId.TRAINER_FIGHT_READY_REQ)
@ProtobufClass
public class TrainerFightReadyReq {

}
