package com.mmorpg.qx.module.trainer.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 10:03 2021/4/7
 */
@SocketPacket(packetId = PacketId.TRAINER_LIST_REQ)
@ProtobufClass
public class TrainerListReq {

}
