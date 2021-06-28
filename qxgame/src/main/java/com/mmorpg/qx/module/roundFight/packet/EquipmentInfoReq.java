package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 请求装备栏信息
 * @since 11:55 2020-10-19
 */
@SocketPacket(packetId = PacketId.EQUIPMENT_INFO_REQ)
@ProtobufClass
public class EquipmentInfoReq {

}
